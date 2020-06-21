import java.time.Instant;
import java.util.Random;

import app.library.annotations.*;
import app.library.*;

public class RandomStrategy implements ExecutableStrategy
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

	@StrategyParameter("Open Probability (0-1)")
	private double openProbability;
	@StrategyParameter("Close Probability (0-1)")
	private double closeProbability;
	@StrategyParameter("Trade Amount (0-1]")
	private double amount;

	@Override
	public String name()
	{
		return "Random Strategy";
	}

	@Override
	public boolean validate()
	{
		return openProbability > 0 && openProbability < 1 && closeProbability > 0 && closeProbability < 1 && amount > 0 && amount <= 1;
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		Random rand = new Random();
		if (rand.nextDouble() <= openProbability)
			journal.openTrade(journal.availAmount() * amount);
		if (rand.nextDouble() <= closeProbability && journal.openTradesCount() > 1)
			journal.closeTrade(journal.oldestTrade());
	}

	public void finish(Journal journal)
	{
		journal.closeAll();
	}
}
