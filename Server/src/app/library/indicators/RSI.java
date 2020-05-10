package app.library.indicators;

import java.util.ArrayList;
import java.util.List;

import app.library.Candle;

public class RSI extends Indicator
{
	private double value;
	private int period;
	private RS rs;

	public RSI(int period)
	{
		this.period = period;
		this.rs = new RS(period);
	}

	@Override
	public List<Indicator> depends()
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		indicators.add(rs);
		return indicators;
	}

	@Override
	public void compute(Candle candle)
	{
		rs.compute(candle);
		value = 100 - (100/(1+rs.getValue()));
	}

	public double getValue()
	{
		return this.value;
	}

	public int getPeriod()
	{
		return period;
	}

	public RS getRS()
	{
		return rs;
	}
}
