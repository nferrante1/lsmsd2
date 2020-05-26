package app.library.indicators;

import java.util.ArrayList;
import java.util.List;

import app.library.Candle;
import app.library.indicators.enums.InputPrice;

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

	@Override
	public List<Indicator> depends()
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		indicators.add(smau);
		indicators.add(smad);
		return indicators;
	}

	@Override
	public void compute(Candle candle)
	{
		smau.compute(candle);
		smad.compute(candle);
		double smauV = smau.getValue();
		double smadV = smad.getValue();
		if(Double.isNaN(smadV) && Double.isNaN(smauV))
			value = Double.NaN;
		else if (Double.isNaN(smadV))
			value = Double.POSITIVE_INFINITY;
		else if (Double.isNaN(smauV))
			value = 0.0;
		else
			value =  smauV / smadV;
	}

	public double getValue()
	{
		return this.value;
	}

	public SMA getSMAu()
	{
		return smau;
	}

	public SMA getSMAd()
	{
		return smad;
	}

	public int getPeriod()
	{
		return period;
	}
}
