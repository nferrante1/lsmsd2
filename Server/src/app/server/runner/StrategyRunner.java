package app.server.runner;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import app.datamodel.DataRangeManager;
import app.datamodel.DataSourceManager;
import app.datamodel.PojoCursor;
import app.datamodel.StorablePojoCursor;
import app.datamodel.pojos.DataRange;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.Market;
import app.datamodel.pojos.Report;
import app.datamodel.pojos.StrategyRun;
import app.library.Candle;
import app.library.ExecutableStrategy;
import app.library.Journal;
import app.library.annotations.StrategyParameter;
import app.library.indicators.ComputableIndicator;
import app.library.indicators.Indicator;
import app.server.runner.exceptions.StrategyRunException;

public class StrategyRunner extends Thread
{
	private ExecutableStrategy strategy;
	private String marketId;
	private int granularity;
	private boolean inverseCross;
	private Map<String, Object> parameters;
	private Journal journal;
	private double progress = -1.0;
	private Throwable exception;

	public StrategyRunner(ExecutableStrategy strategy, Map<String, Object> parameters)
	{
		this.strategy = strategy;
		this.parameters = parameters;
		if (!parameters.containsKey("market"))
			throw new IllegalArgumentException("You must specify a market.");
		this.marketId = (String)parameters.get("market");
		if (parameters.containsKey("granularity"))
			this.granularity = (int)parameters.get("granularity");
		if (parameters.containsKey("inverseCross"))
			this.inverseCross = (boolean)parameters.get("inverseCross");
	}

	@Override
	public void run()
	{
		progress(-1.0);
		try {
			executeStrategy();
		} catch (Throwable ex) {
			exception = ex;
			return;
		}
		progress(1.0);
	}

	private void executeStrategy()
	{
		if (!marketId.contains(":"))
			throw new StrategyRunException("Invalid market id '" + marketId + "'.");
		String[] split = marketId.split(":", 2);
		DataSourceManager sourceManager = new DataSourceManager();
		StorablePojoCursor<DataSource> cursor = (StorablePojoCursor<DataSource>)sourceManager.find(split[0],
			Projections.fields(Projections.include("enabled"), Projections.elemMatch("markets", Filters.eq("id", split[1]))), null);
		if (!cursor.hasNext())
			throw new StrategyRunException("Can not find source '" + split[0] + "'.");
		DataSource source = cursor.next();
		if (!source.isEnabled())
			throw new StrategyRunException("Can not run strategies on source '" + source.getName() + "'.");
		Market market = source.getMarket(split[1]);
		if (market == null || !market.isSelectable())
			throw new StrategyRunException("Can not find market '" + marketId + "'.");
		int marketGranularity = market.getGranularity();
		if (granularity == 0)
			granularity = marketGranularity;
		else if (granularity < marketGranularity || granularity % marketGranularity != 0)
			throw new StrategyRunException("Invalid granularity.");
		DataRangeManager drManager = new DataRangeManager();
		DataRange range = drManager.get(marketId);
		if (range.start == null)
			throw new StrategyRunException("No data available for the selected market.");
		if (parameters.containsKey("startTime")) {
			Instant value = (Instant)parameters.get("startTime");
			if (value != null && !value.isBefore(range.start))
				range.start = value;
		}
		if (parameters.containsKey("endTime")) {
			Instant value = (Instant)parameters.get("endTime");
			if (value != null && !value.isAfter(range.end))
				range.end = value;
		}
		if (!range.end.isAfter(range.start))
			throw new StrategyRunException("Invalid time range specified: endTime must be after startTime.");

		parameters.put("startTime", range.start);
		parameters.put("endTime", range.end);
		int i = 0;
		Field[] fields = strategy.getClass().getDeclaredFields();
		for (Field field: fields) {
			field.setAccessible(true);
			i++;
			if (!field.isAnnotationPresent(StrategyParameter.class))
				continue;
			String parameterName = field.getAnnotation(StrategyParameter.class).value();
			parameterName = parameterName.isBlank() ? field.getName() : parameterName;
			if (!parameters.containsKey(parameterName))
				throw new StrategyRunException("Parameter '" + parameterName + "' not set.");
			try {
				field.set(strategy, parameters.get(parameterName));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new StrategyRunException("Can not set strategy parameter '" + field.getName() + "'.", e);
			}
			progress(0.1 + (i / fields.length) * 0.1);
		}
		if (!strategy.validate())
			throw new StrategyRunException("Supplied an invalid parameter to the strategy.");

		List<Indicator> indicators = strategy.indicators();
		HashMap<String, List<Bson>> pipelines = getPipelines(indicators);

		progress(0.0);
		AggregationRunner aggregationRunner = new AggregationRunner(marketId, inverseCross, granularity, range);
		PojoCursor<Candle> candleCursor = aggregationRunner.runAggregation(pipelines);
		progress(0.8);

		if (!candleCursor.hasNext())
			throw new StrategyRunException("No data available for the selected market.");
		Candle firstCandle = candleCursor.next();
		firstCandle.setGranularity(granularity);

		journal = new Journal(granularity, firstCandle.getOpenTime(), firstCandle.getOpen());
		for(Indicator indicator: indicators)
			indicator.compute(firstCandle);
		strategy.init(journal);
		strategy.process(journal, firstCandle);

		long steps = (range.end.getEpochSecond() - range.start.getEpochSecond()) / (granularity * 60);
		long curStep = 1;

		while(candleCursor.hasNext() && journal.hasAmount()) {
			Candle candle = candleCursor.next();
			candle.setGranularity(granularity);
			journal.setCurrentCandle(candle);
			for(Indicator indicator: indicators)
				indicator.compute(candle);
			strategy.process(journal, candle);
			progress(0.8 + (curStep / steps) * 0.2);
			curStep++;
		}

		progress(0.99);
		strategy.finish(journal);
	}

	private HashMap<String, List<Bson>> getPipelines(List<Indicator> indicators)
	{
		HashMap<String, List<Bson>> map = new HashMap<String, List<Bson>>();
		for(Indicator indicator: indicators) {
			List<Indicator> dependencies = indicator.depends();
			HashMap<String, List<Bson>> deps = getPipelines(dependencies);
			map.putAll(deps);
			if(ComputableIndicator.class.isAssignableFrom(indicator.getClass())) {
				List<Bson> pipeline = ((ComputableIndicator)indicator).getPipeline();
				String name = ((ComputableIndicator)indicator).getName();
				map.putIfAbsent(name, pipeline);
			}
		}
		return map;
	}

	private synchronized void progress(double progress)
	{
		this.progress = progress;
	}

	public synchronized double progress()
	{
		return this.progress;
	}

	public Throwable getException()
	{
		return exception;
	}

	public String getMarketId()
	{
		return marketId;
	}

	public StrategyRun generateStrategyRun()
	{
		Report report = journal.generateReport();
		return new StrategyRun(parameters, report);
	}
}
