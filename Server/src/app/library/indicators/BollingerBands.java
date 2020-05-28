package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import app.library.Candle;
import app.library.indicators.enums.InputPrice;

// Bollinger's Bands
public class BollingerBands extends Indicator
{
	private int period;
	private double bolu = Double.NaN;
	private double bold = Double.NaN;
	private int distance;
	private SMA ma;
	private StdDev sigma;

	public BollingerBands(int period, int distance)
	{
		this.period = period;
		this.distance = distance;
		this.sigma = new StdDev(period, InputPrice.TYPICAL);
		this.ma = new SMA(period, InputPrice.TYPICAL);
	}

	public BollingerBands()
	{
		this(20);
	}

	public BollingerBands(int period)
	{
		this(period, 2);
	}

	@Override
	public List<Indicator> depends()
	{
		return Arrays.asList(ma, sigma);
	}

	@Override
	public void compute(Candle candle)
	{
		ma.compute(candle);
		sigma.compute(candle);

		double mav = ma.value();
		double sigmav = sigma.value();

		if(Double.isNaN(mav) || Double.isNaN(sigmav)) {
			bolu = Double.NaN;
			bold = Double.NaN;
		} else {
			bolu = mav + distance*sigmav;
			bold = mav - distance*sigmav;
		}
	}

	public int period()
	{
		return period;
	}

	public double upperLine()
	{
		return bolu;
	}

	public double lowerLine()
	{
		return bold;
	}

	public int distance()
	{
		return distance;
	}

	public SMA ma()
	{
		return ma;
	}

	public StdDev sigma()
	{
		return sigma;
	}
}
