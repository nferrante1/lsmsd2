package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import app.library.Candle;

// Stochastic Relative Strength Index
public class StochRSI extends Indicator
{
	private RSI rsi;
	private int index;
	private int stochIndex;
	private int kIndex;
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
		if(period <= 0)
			throw new IllegalArgumentException("'period' argument to StochRSI constructor must be a positive integer (supplied: " + period + ").");
		if(stochPeriod <= 0)
			throw new IllegalArgumentException("'stochPeriod' argument to StochRSI constructor must be a positive integer (supplied: " + stochPeriod + ").");
		if(kPeriod <= 0)
			throw new IllegalArgumentException("'kPeriod' argument to StochRSI constructor must be a positive integer (supplied: " + kPeriod + ").");
		if (rsi == null)
			rsi = new RSI();
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

	public StochRSI(RSI rsi, int period, int stochPeriod, int kPeriod)
	{
		this(period, stochPeriod, kPeriod, rsi);
	}

	public StochRSI(int period, int stochPeriod, int rsiPeriod)
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
		return Arrays.asList(rsi);
	}

	@Override
	public void compute(Candle candle)
	{
		rsi.compute(candle);
		previousRSI[index] = rsi.value();

		double minRSI = Double.NaN;
		for (int i = 0; i < previousRSI.length; i++)
			if (!Double.isNaN(previousRSI[i]) && (Double.isNaN(minRSI) || previousRSI[i] < minRSI))
				minRSI = previousRSI[i];

		double maxRSI = Double.NaN;
		for (int i = 0; i < previousRSI.length; i++)
			if (!Double.isNaN(previousRSI[i]) && (Double.isNaN(minRSI) || previousRSI[i] > maxRSI))
				maxRSI = previousRSI[i];

		if(Double.isNaN(minRSI) || Double.isNaN(maxRSI))
			value = Double.NaN;
		else
			value = ((previousRSI[index] - minRSI) / (maxRSI - minRSI)) * 100;

		index = (index + 1) % period;

		if(Double.isNaN(value))
			return;

		double sum = 0.0;
		int count = 0;
		previousStochRSI[stochIndex] = value;
		stochIndex = (stochIndex + 1) % stochPeriod;
		for (int i = 0; i < previousStochRSI.length; i++) {
			if (Double.isNaN(previousStochRSI[i]))
				continue;
			sum += previousStochRSI[i];
			count++;
		}
		KValue  = sum / count;

		previousK[kIndex] = KValue;
		kIndex = (kIndex + 1) % kPeriod;
		sum = 0.0;
		count = 0;
		for (int i = 0; i < previousK.length; i++) {
			if (Double.isNaN(previousK[i]))
				continue;
			sum += previousK[i];
			count++;
		}
		DValue = sum / count;
	}

	public int period()
	{
		return period;
	}

	public double value()
	{
		return value;
	}

	public RSI rsi()
	{
		return rsi;
	}

	public int stochPeriod()
	{
		return stochPeriod;
	}

	public int kPeriod()
	{
		return kPeriod;
	}

	public double kValue()
	{
		return KValue;
	}

	public double dValue()
	{
		return DValue;
	}
}
