package app.datamodel.pojos;

import java.time.Instant;

import com.google.gson.annotations.SerializedName;

public class Candle extends Pojo
{

	protected Instant time;
	protected double open;
	protected double high;
	protected double low;
	protected double close;
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
	
	public Candle()
	{
		this.time = null;
		this.open = 0;
		this.high = 0;
		this.low = 0;
		this.close = 0;
		this.volume = 0;
	}
	
	public Candle(Instant time)
	{
		this.time = time;
		this.open = 0;
		this.high = 0;
		this.low = 0;
		this.close = 0;
		this.volume = 0;
	}
	
	public void setTime(Instant time)
	{
		updateField("time", time);
	}

	public void setOpen(double open)
	{
		updateField("open", open);
	}

	public void setHigh(double high)
	{
		updateField("high", high);
	}

	public void setLow(double low)
	{
		updateField("low", low);
	}

	public void setClose(double close)
	{
		updateField("close", close);
	}

	public void setVolume(double volume)
	{
		updateField("volume", volume);
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
