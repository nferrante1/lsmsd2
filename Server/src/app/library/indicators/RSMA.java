package app.library.indicators;

import java.util.List;

import org.bson.conversions.Bson;

import app.library.Candle;

public class RSMA extends Indicator implements ComputableIndicator
{
	private int period;
	private boolean increment;
	private double value;

	public double getValue()
	{
		return this.value;
	}

	public RSMA(int period, boolean increment)
	{
		this.period = period;
		this.increment = increment;
	}

	@Override
	public String getName()
	{
		return "SMA" + (increment ? "u" : "d") + period;
	}

	@Override
	public List<Bson> getPipeline()
	{
		return null;
	}

	@Override
	public void compute(Candle candle)
	{
		value = candle.getTa(getName());
	}
}
