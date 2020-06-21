import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import app.library.annotations.*;
import app.library.indicators.*;
import app.library.*;

public class MACDStrategy implements ExecutableStrategy
{
	@StrategyParameter
	private String market;
	@StrategyParameter
	private boolean inverseCross;
	@StrategyParameter
	private int granularity;
	@StrategyParameter
	private Instant startTime;
	@StrategyParameter
	private Instant endTime;
	@StrategyParameter("Long EMA Period (>0)")
	private int longEMAPeriod;
	@StrategyParameter("Short EMA Period (>0)")
	private int shortEMAPeriod;
	@StrategyParameter("MACD Period (>0)")
	private int MACDPeriod;

	private MACD macd;
	private double previousMACD;

	@Override
	public String name()
	{
		return "MACD strategy";
	}

	@Override
	public boolean validate()
	{
		return shortEMAPeriod > 0 && longEMAPeriod > shortEMAPeriod && MACDPeriod > 0;
	}

	@Override
	public List<Indicator> indicators()
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		macd = new MACD(shortEMAPeriod, longEMAPeriod, MACDPeriod);
		indicators.add(macd);
		return indicators;
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		double value = macd.hist();
		if(Double.isNaN(value))
			return;
		if(Double.isNaN(previousMACD))
		{
			previousMACD = value;
			return;
		}

		if(previousMACD <= 0 && value > 0)
			journal.allIn();
		if(previousMACD >= 0 && value < 0)
			journal.closeAll();

		previousMACD = value;
	}
}
