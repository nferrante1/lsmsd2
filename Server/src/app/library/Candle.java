package app.library;

import java.time.Instant;
import java.util.HashMap;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Candle //TODO
{
	@BsonProperty("t")
	protected Instant time;
	@BsonProperty("o")
	protected double open;
	@BsonProperty("h")
	protected double high;
	@BsonProperty("l")
	protected double low;
	@BsonProperty("c")
	protected double close;
	@BsonProperty("v")
	protected double volume;
	protected HashMap<String, Double> ta;

	public Candle()
	{
	}

	public Instant getTime()
	{
		return time;
	}
	public void setTime(Instant time)
	{
		this.time = time;
	}
	public double getOpen()
	{
		return open;
	}
	public void setOpen(double open)
	{
		this.open = open;
	}
	public double getHigh()
	{
		return high;
	}
	public void setHigh(double high)
	{
		this.high = high;
	}
	public double getLow()
	{
		return low;
	}
	public void setLow(double low)
	{
		this.low = low;
	}
	public double getClose()
	{
		return close;
	}
	public void setClose(double close)
	{
		this.close = close;
	}
	public double getVolume()
	{
		return volume;
	}
	public void setVolume(Double volume)
	{
		this.volume = volume;
	}
	public HashMap<String, Double> getTa()
	{
		return ta;
	}
	public void setTa(HashMap<String, Double> ta)
	{
		this.ta = ta;
	}

	@BsonIgnore
	public double getTa(String name)
	{
		return ta.get(name);
	}
}
