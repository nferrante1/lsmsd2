import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.library.annotations.*;
import app.library.indicators.*;
import app.library.*;

public class RSMAStrategy implements ExecutableStrategy
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

	@StrategyParameter("RSMA Period")
	private int period;
	@StrategyParameter("Ascending?")
	private boolean ascending;

	private RSMA rsma;

	@Override
	public String getName()
	{
		return "Simple RSMA strategy";
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		System.out.println("T: " + candle.getTime()
			+ " | O: " + candle.getOpen()
			+ " | H: " + candle.getHigh()
			+ " | L: " + candle.getLow()
			+ " | C: " + candle.getClose()
			+ " | V: " + candle.getVolume()
			+ " | RSMAu14: " + rsma.getValue()
		);
	}

	@Override
	public void finish(Journal journal)
	{
		System.out.println("Market: " + market + " (" + (inverseCross ? "inverted" : "direct") + ")");
		System.out.println("Granularity: " + granularity);
		System.out.println("Start Time: " + startTime);
		System.out.println("End Time: " + endTime);
		System.out.println("RSMA Period: " + period);
		System.out.println("Ascending: " + (ascending ? "true" : "false"));
	}

	public RSMAStrategy()
	{
	}

	@Override
	public List<Indicator> getIndicators()
	{
		List<Indicator> ind = new ArrayList<Indicator>();
		rsma = new RSMA(period, ascending);
		ind.add(rsma);
		return ind;
	}

}
