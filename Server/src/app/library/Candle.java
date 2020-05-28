package app.library;

import java.time.Instant;
import java.util.HashMap;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public final class Candle
{
	@BsonProperty("t")
	private Instant openTime;
	@BsonProperty("o")
	private double open;
	@BsonProperty("h")
	private double high;
	@BsonProperty("l")
	private double low;
	@BsonProperty("c")
	private double close;
	@BsonProperty("v")
	private double volume;
	@BsonProperty("ta")
	private HashMap<String, Double> ta;
	private transient int granularity;

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
		if (ta == null || !ta.containsKey(name) || ta.get(name) == null)
			return Double.NaN;
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

	public double getRange()
	{
		return high - low;
	}

	public double getTrueRange()
	{
		return Math.max(high - low, Math.max(Math.abs(high - close), Math.abs(low - close)));
	}

	public double getTypicalPrice()
	{
		return (high + low + close) / 3;
	}

	public double getIncrement()
	{
		return Math.max(close - open, 0);
	}

	public double getDecrement()
	{
		return Math.max(open - close, 0);
	}
}
