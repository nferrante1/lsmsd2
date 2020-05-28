package app.library.indicators;

import app.library.Candle;

// On-Balance Volume
public class OBV extends Indicator
{
	private double value;

	@Override
	public void compute(Candle candle)
	{
		double diff = candle.getClose() - candle.getOpen();
		if (diff == 0)
			return;
		value += (diff > 0 ? 1 : -1) * candle.getVolume();
	}

	public double value()
	{
		return value;
	}
}
