import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import app.library.annotations.*;
import app.library.indicators.*;
import app.library.*;

public class BollingerBandsStrategy implements ExecutableStrategy
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
	@StrategyParameter("Bollinger Bands Period (>0)")
	private int period;
	@StrategyParameter("Bands Distance (>0)")
	private int distance;

	private BollingerBands bb;
	private double previousClose = Double.NaN;

	@Override
	public String name()
	{
		return "Bollinger Bands Strategy";
	}

	@Override
	public boolean validate()
	{
		return period > 0 && distance > 0;
	}

	@Override
	public List<Indicator> indicators()
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		bb = new BollingerBands(period, distance);
		indicators.add(bb);
		return indicators;
	}


	@Override
	public void process(Journal journal, Candle candle)
	{
		double upper = bb.upperLine();
		double lower = bb.lowerLine();
		if(Double.isNaN(upper) || Double.isNaN(lower))
		{
			previousClose = candle.getClose();
			return;
		}
		if (previousClose <= lower && candle.getClose() > lower) // Crossover
			journal.allIn();
		if (previousClose >= upper && candle.getClose() < upper) // Crossunder
			journal.closeAll();
		previousClose = candle.getClose();
	}
}
