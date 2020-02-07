package app.scraper.data;

import java.time.Instant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Candle
{
	@SerializedName(value = "t")
	@Expose
	protected Instant time;
	@Expose
	@SerializedName(value = "o")
	protected double open;
	@SerializedName(value = "h")
	@Expose
	protected double high;
	@SerializedName(value = "l")
	@Expose
	protected double low;
	@SerializedName(value = "c")
	@Expose
	protected double close;
	@SerializedName(value = "v")
	@Expose
	protected double volume;
	
	public Candle(Instant time, double open, double high, double low, double close, double volume)
	{
		this.time = time;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}
	
	private Candle()
	{
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
