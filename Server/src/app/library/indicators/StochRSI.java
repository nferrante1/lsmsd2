package app.library.indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.library.Candle;

public class StochRSI extends Indicator
{
	private RSI rsi;
	private int index = 0;
	private int period;
	private double previousRSI[];

	private double value = Double.NaN;

	public StochRSI(int period, RSI rsi)
	{
		this.period = period;
		this.rsi = rsi;
		previousRSI = new double[period];
		Arrays.fill(previousRSI, -1.0);
	}

	public StochRSI(int period, int rsiPeriod)
	{
		this(period, new RSI(rsiPeriod));
	}

	public StochRSI(int period)
	{
		this(period, period);
	}

	@Override
	public List<Indicator> depends()
	{
		List<Indicator> indicators = new ArrayList<Indicator>(1);
		indicators.add(rsi);
		return indicators;
	}

	@Override
	public void compute(Candle candle)
	{
		rsi.compute(candle);
		previousRSI[index] = rsi.getValue();
		double minRSI = Double.MAX_VALUE;
		for (int i = 0; i < previousRSI.length; i++)
			if (previousRSI[i] >= 0.0 && previousRSI[i] < minRSI)
				minRSI = previousRSI[i];
		double maxRSI = Double.MIN_VALUE;
		for (int i = 0; i < previousRSI.length; i++)
			if (previousRSI[i] >= 0.0 && previousRSI[i] > maxRSI)
				maxRSI = previousRSI[i];
		value = ((previousRSI[index] - minRSI) / (maxRSI - minRSI)) * 100;
		index = (index + 1) % period;
	}

	public int getPeriod()
	{
		return period;
	}

	public double getValue()
	{
		return value;
	}

	public RSI getRSI()
	{
		return rsi;
	}
}
