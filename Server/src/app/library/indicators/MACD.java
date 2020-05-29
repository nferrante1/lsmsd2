package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import app.library.Candle;

// Moving Average Convergence/Divergence
public class MACD extends Indicator
{
	private int period;
	private EMA shortEMA;
	private EMA longEMA;
	private double previousMACD[];
	private double value = Double.NaN;
	private int index;
	private double signal;
	private double hist;

	public MACD()
	{
		this(12, 26, 9);
	}

	public MACD(int shortPeriod, int longPeriod, int period)
	{
		if(period <= 0)
			throw new IllegalArgumentException("Last argument to MACD constructor must be a positive integer (supplied: " + period + ").");
		this.longEMA = new EMA(longPeriod);
		this.shortEMA = new EMA(shortPeriod);
		this.period = period;
		this.previousMACD = new double[period];
		Arrays.fill(previousMACD, Double.NaN);
	}

	@Override
	public void compute(Candle candle)
	{
		shortEMA.compute(candle);
		longEMA.compute(candle);
		double sema = shortEMA.value();
		double lema = longEMA.value();
		if(Double.isNaN(sema) || Double.isNaN(lema))
		{	value = Double.NaN;
			return;
		}
		
		value = sema - lema;
		previousMACD[index] = value;
		index = (index + 1)%period;
		
		double sum = 0;
		int count = 0;
		for(int i = 0; i< previousMACD.length ; ++i) {
			if(Double.isNaN(previousMACD[i]))
				continue;
			sum += previousMACD[i];
			count++;
		}
		if(count == 0)
			return;
		
		signal = sum/count;
		hist = value - signal;
		
	}

	@Override
	public List<Indicator> depends()
	{
		return Arrays.asList(longEMA, shortEMA);
	}

	public EMA shortEMA()
	{
		return shortEMA;
	}

	public EMA longEMA()
	{
		return longEMA;
	}

	public double value()
	{
		return value;
	}

	public int period()
	{
		return period;
	}

	public double signal()
	{
		return signal;
	}

	public double hist()
	{
		return hist;
	}
}
