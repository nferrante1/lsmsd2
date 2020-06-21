import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import app.library.annotations.*;
import app.library.indicators.*;
import app.library.*;

public class GoldenCrossStrategy implements ExecutableStrategy
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

	@StrategyParameter("Long SMA Period (>0)")
	private int longSMAPeriod;
	@StrategyParameter("Short SMA Period (>0)")
	private int shortSMAPeriod;

	private SMA longSMA;
	private SMA shortSMA;
	private Boolean wasShortAbove;

	@Override
	public String name()
	{
		return "Golden Cross Strategy";
	}

	@Override
	public boolean validate()
	{
		return shortSMAPeriod > 0 && longSMAPeriod > shortSMAPeriod;
	}

	@Override
	public List<Indicator> indicators()
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		longSMA = new SMA(longSMAPeriod);
		shortSMA = new SMA(shortSMAPeriod);
		indicators.add(longSMA);
		indicators.add(shortSMA);
		return indicators;
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		double longVal = longSMA.value();
		double shortVal = shortSMA.value();
		if (Double.isNaN(longVal) || Double.isNaN(shortVal))
			return;
		if (wasShortAbove == null) {
			wasShortAbove = (shortVal == longVal) ? null : shortVal > longVal;
			return;
		}

		if (shortVal > longVal && !wasShortAbove) // Crossover
			journal.allIn();
		if (shortVal < longVal && wasShortAbove) // Crossunder
			journal.closeAll();

		if (shortVal != longVal)
			wasShortAbove = shortVal > longVal;
	}
}
