package app.datamodel.pojos;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonProperty;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.annotations.PojoId;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("MarketData")
public final class Candle extends StorablePojo
{
	@PojoId
	@BsonProperty("t")
	private Instant time;
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

	public Candle(Instant time, double open, double high, double low, double close, double volume)
	{
		super(StorablePojoState.UNTRACKED);
		this.time = time;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

	public Candle()
	{
		super();
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
