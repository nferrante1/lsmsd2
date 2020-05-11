package app.server.runner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;

import app.datamodel.PojoCursor;
import app.library.Candle;
import app.library.ExecutableStrategy;
import app.library.indicators.ComputableIndicator;
import app.library.indicators.Indicator;
import app.server.managers.CandleManager;

public class StrategyRunner extends Thread
{
	private ExecutableStrategy strategy;
	private String marketId;
	private int granularity;
	private boolean inverseCross;
	private Map<String, Object> parameters;

	public StrategyRunner(ExecutableStrategy strategy, Map<String, Object> parameters)
	{
		this.strategy = strategy;
		this.parameters = parameters;
		this.marketId = (String)parameters.get("market");
		this.granularity = (int)parameters.get("granularity");
		this.inverseCross = (boolean)parameters.get("inverseCross");
	}

	@Override
	public void run()
	{
		for (Map.Entry<String, Object> parameter: parameters.entrySet()) {
			try {
				Field field = strategy.getClass().getDeclaredField(parameter.getKey());
				field.setAccessible(true);
				field.set(strategy, parameter.getValue());
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			}
		}

		List<Indicator> indicators = strategy.getIndicators();
		HashMap<String, List<Bson>> map = getPipelines(indicators);

		CandleManager candleManager = new CandleManager();
		PojoCursor<Candle> candleCursor = candleManager.getCandles(marketId, inverseCross, granularity, map);

		while(candleCursor.hasNext()) {
			Candle candle = candleCursor.next();
			for(Indicator indicator: indicators)
				indicator.compute(candle);
			strategy.process(candle);
		}
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
}
