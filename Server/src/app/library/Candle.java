package app.library;

import java.time.Instant;
import java.util.HashMap;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Candle
{
	@BsonProperty("t")
	protected Instant openTime;
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
	@BsonProperty("ta")
	protected HashMap<String, Double> ta;
	protected transient int granularity;

	public Candle()
	{
	}

	public Instant getOpenTime()
	{
		return openTime;
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

	public HashMap<String, Double> getTa()
	{
		return ta;
	}

	@BsonIgnore
	public double getTa(String name)
	{
		return ta.get(name);
	}

	@BsonIgnore
	public Instant getTime()
	{
		return getCloseTime();
	}

	@BsonIgnore
	public Instant getCloseTime()
	{
		return openTime.plusSeconds(granularity * 60);
	}

	public void setGranularity(int granularity)
	{
		this.granularity = granularity;
	}

	public void setTa(HashMap<String, Double> ta)
	{
		this.ta = ta;
	}

	public void setVolume(Double volume)
	{
		this.volume = volume;
	}

	public void setClose(double close)
	{
		this.close = close;
	}

	public void setLow(double low)
	{
		this.low = low;
	}

	public void setHigh(double high)
	{
		this.high = high;
	}

	public void setOpen(double open)
	{
		this.open = open;
	}

	public void setOpenTime(Instant openTime)
	{
		this.openTime = openTime;
	}
}
