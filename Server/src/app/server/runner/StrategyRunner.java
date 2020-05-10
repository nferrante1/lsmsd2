package app.server.runner;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bson.conversions.Bson;

import app.datamodel.PojoCursor;
import app.library.Candle;
import app.library.ExecutableStrategy;
import app.library.indicators.ComputableIndicator;
import app.library.indicators.Indicator;
import app.server.StrategyFile;
import app.server.dm.CandleManager;

public class StrategyRunner extends Thread
{
	private StrategyFile strategyFile;

	public StrategyRunner(StrategyFile file)
	{
		setStrategyFile(file);
	}

	@Override
	public void run()
	{
		Logger.getLogger(StrategyRunner.class.getName()).warning("START STRATEGY EXECUTION");
		ExecutableStrategy strategy = strategyFile.getStrategy();
		List<Indicator> indicators = strategy.getIndicators();
		HashMap<String, List<Bson>> map = getPipelines(indicators);
		CandleManager candleManager = new CandleManager();
		Logger.getLogger(StrategyRunner.class.getName()).warning("START AGGREGATION PIPELINE");
		PojoCursor<Candle> candleCursor = candleManager.getCandles("BINANCE:ADABNB", 10, map);
		Logger.getLogger(StrategyRunner.class.getName()).warning("END AGGREGATION PIPELINE");
		while(candleCursor.hasNext()) {
			Candle candle = candleCursor.next();
			for(Indicator indicator: indicators)
				indicator.compute(candle);
			strategy.process(candle);
		}
		Logger.getLogger(StrategyRunner.class.getName()).warning("END STRATEGY EXECUTION");
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

	public StrategyFile getStrategyFile()
	{
		return strategyFile;
	}
	public void setStrategyFile(StrategyFile strategyFile)
	{
		this.strategyFile = strategyFile;
	}
}
