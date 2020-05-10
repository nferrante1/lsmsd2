package app.library.indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.library.Candle;

public class StochRSID extends Indicator
{
	private StochRSIK stochRSIK;
	private int period;
	private int index = 0;
	private double previousStochRSIK[];

	private double value;

	public StochRSID(int period, StochRSIK stochRSIK)
	{
		this.period = period;
		this.stochRSIK = stochRSIK;
		previousStochRSIK = new double[period];
		Arrays.fill(previousStochRSIK, -1.0);
	}

	public StochRSID(int period, int kPeriod, int stochPeriod, int rsiPeriod)
	{
		this(period, new StochRSIK(kPeriod, stochPeriod, rsiPeriod));
	}

	public StochRSID(int period, int kPeriod, int stochPeriod)
	{
		this(period, kPeriod, stochPeriod, stochPeriod);
	}

	public StochRSID(int period, int kPeriod)
	{
		this(period, kPeriod, kPeriod);
	}

	public StochRSID(int period)
	{
		this(period, period);
	}

	@Override
	public List<Indicator> depends()
	{
		List<Indicator> indicators = new ArrayList<Indicator>(1);
		indicators.add(stochRSIK);
		return indicators;
	}

	@Override
	public void compute(Candle candle)
	{
		stochRSIK.compute(candle);
		previousStochRSIK[index] = stochRSIK.getValue();
		index = (index + 1) % period;
		double sum = 0.0;
		int count = 0;
		for (int i = 0; i < previousStochRSIK.length; i++)
			if (previousStochRSIK[i] >= 0.0) {
				sum += previousStochRSIK[i];
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

	public StochRSIK getK()
	{
		return stochRSIK;
	}
}
