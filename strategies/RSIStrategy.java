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

	@StrategyParameter("RSI period")
	private int period;
	@StrategyParameter("RSI oversold (0-100)")
	private int oversold;
	@StrategyParameter("RSI overbought (0-100)")
	private int overbought;
	@StrategyParameter("Trade amount (0-1)")
	private double amount;

	private double previousRSI = Double.NaN;
	private RSI rsi;

	@Override
	public String getName()
	{
		return "Real RSI Strategy v2";
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		double value = rsi.getValue();
		if (Double.isNaN(previousRSI)) {
			previousRSI = value;
			return;
		}
		if (previousRSI >= overbought && value < overbought) { //crossunder
			journal.closeAll();
		}
		if (previousRSI <= oversold && value > oversold) { //crossover
			if (journal.availAmount() > amount)
				journal.openTrade(amount);
		}
		previousRSI = value;
	}

	@Override
	public void finish(Journal journal)
	{
	}

	public RSIStrategy()
	{
	}

	public boolean validate()
	{
		return oversold >= 0 && overbought > oversold && overbought <= 100 && amount >= 0.0 && amount <= 1.0;
	}

	@Override
	public List<Indicator> indicators()
	{
		List<Indicator> ind = new ArrayList<Indicator>();
		rsi = new RSI(period);
		ind.add(rsi);
		return ind;
	}
}
