import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import app.library.annotations.*;
import app.library.indicators.*;
import app.library.*;

public class SampleStrategy implements ExecutableStrategy
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

	@StrategyParameter("RSI Period")
	private int period;

	private RSI rsi;

	@Override
	public String getName()
	{
		return "Sample strategy based on RSI";
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
			+ " | RSI: " + rsi.getValue()
		);
	}

	@Override
	public void finish(Journal journal)
	{
		System.out.println("Market: " + market + " (" + (inverseCross ? "inverted" : "direct") + ")");
		System.out.println("Granularity: " + granularity);
		System.out.println("Start Time: " + startTime);
		System.out.println("End Time: " + endTime);
		System.out.println("RSI Period: " + period);
	}

	public SampleStrategy()
	{
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
