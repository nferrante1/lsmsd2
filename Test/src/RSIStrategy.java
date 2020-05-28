import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import app.library.annotations.*;
import app.library.indicators.*;
import app.library.*;

public class RSIStrategy implements ExecutableStrategy
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

	@StrategyParameter("RSI Period (>0)")
	private int period;
	@StrategyParameter("RSI Oversold [0-100)")
	private int oversold;
	@StrategyParameter("RSI Overbought (0-100]")
	private int overbought;
	@StrategyParameter("Amount to Trade (0-1]")
	private double amount;

	private double previousRSI = Double.NaN;
	private RSI rsi;

	@Override
	public String name()
	{
		return "RSI Strategy";
	}

	@Override
	public boolean validate()
	{
		return oversold >= 0 && overbought > oversold && overbought <= 100 && amount > 0.0 && amount <= 1.0;
	}

	@Override
	public List<Indicator> indicators()
	{
		List<Indicator> ind = new ArrayList<Indicator>(1);
		rsi = new RSI(period);
		ind.add(rsi);
		return ind;
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		double value = rsi.value();
		if (Double.isNaN(previousRSI)) {
			previousRSI = value;
			return;
		}
		if (previousRSI >= overbought && value < overbought) // crossunder
			journal.closeAll();
		if (previousRSI <= oversold && value > oversold) // crossover
			journal.openTrade(amount * journal.availAmount());
		previousRSI = value;
	}
}
