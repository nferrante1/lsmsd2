package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import app.library.Candle;

// Relative Strength Index
public class RSI extends Indicator
{
	private double value = Double.NaN;
	private int period;
	private RS rs;

	public RSI(int period)
	{
		this.period = period;
		this.rs = new RS(period);
	}

	public RSI()
	{
		this(14);
	}

	@Override
	public List<Indicator> depends()
	{
		return Arrays.asList(rs);
	}

	@Override
	public void compute(Candle candle)
	{
		rs.compute(candle);
		double rsv = rs.value();
		if(Double.isNaN(rsv))
			value = Double.NaN;
		else
			value = 100 - (100/(1 + rsv));
	}

	public double value()
	{
		return value;
	}

	public int period()
	{
		return period;
	}

	public RS rs()
	{
		return rs;
	}
}
