package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import app.library.Candle;

// Moving Average Convergence/Divergence
public class MACD extends Indicator
{
	private EMA shortEMA;
	private EMA longEMA;
	private double value = Double.NaN;

	public MACD()
	{
		this(12, 26);
	}

	public MACD(int shortPeriod, int longPeriod)
	{
		this.longEMA = new EMA(longPeriod);
		this.shortEMA = new EMA(shortPeriod);
	}

	@Override
	public void compute(Candle candle)
	{
		shortEMA.compute(candle);
		longEMA.compute(candle);
		double sema = shortEMA.value();
		double lema = longEMA.value();
		if(Double.isNaN(sema) || Double.isNaN(lema))
			value = Double.NaN;
		else
			value = sema - lema;
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
}
