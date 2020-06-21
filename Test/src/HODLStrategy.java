import java.time.Instant;

import app.library.annotations.*;
import app.library.*;

public class HODLStrategy implements ExecutableStrategy
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

	private Trade trade;

	@Override
	public String name()
	{
		return "HODL Strategy";
	}

	@Override
	public boolean validate()
	{
		return true;
	}

	@Override
	public void init(Journal journal)
	{
		trade = journal.allIn();
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		// Do nothing
	}

	@Override
	public void finish(Journal journal)
	{
		journal.closeTrade(trade);
	}
}
