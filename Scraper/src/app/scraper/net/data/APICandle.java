package app.scraper.net.data;

import java.time.Instant;

public class APICandle
{
	protected Instant time;
	protected double open;
	protected double high;
	protected double low;
	protected double close;
	protected double volume;

	public APICandle(Instant time, double open, double high, double low, double close, double volume)
	{
		this.time = time;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

	public APICandle(Instant time, double price)
	{
		this(time, price, price, price, price, 0.0);
	}

	public Instant getTime()
	{
		return time;
	}

	public double getOpen()
	{
		return open;
	}

	public double getHigh()
	{
		return high;
	}

	public double getLow()
	{
		return low;
	}

	public double getClose()
	{
		return close;
	}

	public double getVolume()
	{
		return volume;
	}

	public void setTime(Instant time)
	{
		this.time = time;
	}
}