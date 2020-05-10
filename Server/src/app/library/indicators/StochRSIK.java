package app.library.indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.library.Candle;

public class StochRSIK extends Indicator
{
	private StochRSI stochRSI;
	private int period;
	private int index = 0;
	private double previousStochRSI[];

	private double value;

	public StochRSIK(int period, StochRSI stochRSI)
	{
		this.period = period;
		this.stochRSI = stochRSI;
		previousStochRSI = new double[period];
		Arrays.fill(previousStochRSI, -1.0);
	}

	public StochRSIK(int period, int stochPeriod, int rsiPeriod)
	{
		this(period, new StochRSI(stochPeriod, rsiPeriod));
	}

	public StochRSIK(int period, int stochPeriod)
	{
		this(period, stochPeriod, stochPeriod);
	}

	public StochRSIK(int period)
	{
		this(period, period);
	}

	@Override
	public List<Indicator> depends()
	{
		List<Indicator> indicators = new ArrayList<Indicator>(1);
		indicators.add(stochRSI);
		return indicators;
	}

	@Override
	public void compute(Candle candle)
	{
		stochRSI.compute(candle);
		previousStochRSI[index] = stochRSI.getValue();
		index = (index + 1) % period;
		double sum = 0.0;
		int count = 0;
		for (int i = 0; i < previousStochRSI.length; i++)
			if (previousStochRSI[i] >= 0.0) {
				sum += previousStochRSI[i];
				count++;
			}
		value = sum / count;
	}

	public int getPeriod()
	{
		return period;
	}

	public double getValue()
	{
		return value;
	}

	public StochRSI getStochRSI()
	{
		return stochRSI;
	}
}
