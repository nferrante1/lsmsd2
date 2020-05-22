package app.library.indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.library.Candle;

public class StochRSI extends Indicator
{
	private RSI rsi;
	private int index = 0;
	private int stochIndex = 0;
	private int kIndex = 0;
	private int period;
	private int stochPeriod;
	private int kPeriod;
	private double previousRSI[];
	private double previousStochRSI[];
	private double previousK[];

	private double value = Double.NaN;
	private double KValue = Double.NaN;
	private double DValue = Double.NaN;

	public StochRSI(int period, int stochPeriod, int kPeriod, RSI rsi)
	{
		this.period = period;
		this.stochPeriod = stochPeriod;
		this.rsi = rsi;
		this.kPeriod = kPeriod;
		previousRSI = new double[period];
		Arrays.fill(previousRSI, Double.NaN);
		previousStochRSI = new double[stochPeriod];
		Arrays.fill(previousStochRSI, Double.NaN);
		previousK = new double[kPeriod];
		Arrays.fill(previousK, Double.NaN);
	}

	public StochRSI(int period, int stochPeriod,int rsiPeriod)
	{
		this(period, stochPeriod, stochPeriod, new RSI(rsiPeriod));
	}

	public StochRSI(int period, int stochPeriod, int kPeriod, int rsiPeriod)
	{
		this(period, stochPeriod, kPeriod, new RSI(rsiPeriod));
	}

	public StochRSI(int period, int rsiPeriod)
	{
		this(period, period, period, new RSI(rsiPeriod));
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
		double minRSI = Double.NaN;

		for (int i = 0; i < previousRSI.length; i++)
			if (!Double.isNaN(previousRSI[i]) && (Double.isNaN(minRSI) || previousRSI[i] < minRSI))
				minRSI = previousRSI[i];
		double maxRSI = Double.NaN;

		for (int i = 0; i < previousRSI.length; i++)
			if (!Double.isNaN(previousRSI[i]) && (Double.isNaN(minRSI) || previousRSI[i] > maxRSI))
				maxRSI = previousRSI[i];
		if(!Double.isNaN(minRSI) && !Double.isNaN(maxRSI))
			value = ((previousRSI[index] - minRSI) / (maxRSI - minRSI)) * 100;

		index = (index + 1) % period;

		if(Double.isNaN(value))
			return;

		previousStochRSI[stochIndex] = value;
		stochIndex = (stochIndex + 1) % stochPeriod;
		double sum = 0.0;
		int count = 0;
		for (int i = 0; i < previousStochRSI.length; i++)
			if (!Double.isNaN(previousStochRSI[i])) {
				sum += previousStochRSI[i];
				count++;
			}
		KValue  = sum / count;

		previousK[kIndex] = KValue;
		kIndex = (kIndex + 1) % kPeriod;
		sum = 0.0;
		count = 0;
		for (int i = 0; i < previousK.length; i++)
			if (!Double.isNaN(previousK[i])) {
				sum += previousK[i];
				count++;
			}
		DValue = sum / count;
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

	public int getStochPeriod()
	{
		return stochPeriod;
	}

	public int getkPeriod()
	{
		return kPeriod;
	}

	public double getKValue()
	{
		return KValue;
	}

	public double getDValue()
	{
		return DValue;
	}
}
