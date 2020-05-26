package app.scraper.net.data;

import java.time.Instant;

public final class APICandle
{
	private final Instant time;
	private final double open;
	private final double high;
	private final double low;
	private final double close;
	private final double volume;

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
}