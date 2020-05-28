package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import app.library.Candle;
import app.library.indicators.enums.InputPrice;

// Relative Strength
public class RS extends Indicator
{
	private int period;
	private SMA smau;
	private SMA smad;
	private double value = Double.NaN;

	public RS(int period)
	{
		this.smau = new SMA(period, InputPrice.INCREMENT);
		this.smad = new SMA(period, InputPrice.DECREMENT);
		this.period = period;
	}

	public RS()
	{
		this(14);
	}

	@Override
	public List<Indicator> depends()
	{
		return Arrays.asList(smau, smad);
	}

	@Override
	public void compute(Candle candle)
	{
		smau.compute(candle);
		smad.compute(candle);
		double smauV = smau.value();
		double smadV = smad.value();
		if(Double.isNaN(smadV) && Double.isNaN(smauV))
			value = Double.NaN;
		else if (Double.isNaN(smadV))
			value = Double.POSITIVE_INFINITY;
		else if (Double.isNaN(smauV))
			value = 0.0;
		else
			value =  smauV / smadV;
	}

	public double value()
	{
		return value;
	}

	public SMA smau()
	{
		return smau;
	}

	public SMA smad()
	{
		return smad;
	}

	public int period()
	{
		return period;
	}
}
