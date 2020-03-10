package app.datamodel;

import java.time.Instant;

import com.google.gson.annotations.SerializedName;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.EmbeddedPojo;
import app.datamodel.mongo.EmbeddedPojoManager;

@CollectionName("MarketData")
public class Candle extends EmbeddedPojo
{
	@SerializedName(value = "t")
	protected Instant time;
	@SerializedName(value = "o")
	protected double open;
	@SerializedName(value = "h")
	protected double high;
	@SerializedName(value = "l")
	protected double low;
	@SerializedName(value = "c")
	protected double close;
	@SerializedName(value = "v")
	protected double volume;
	private static transient EmbeddedPojoManager<Candle> manager;
	
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
	
	public static EmbeddedPojoManager<Candle> getManager()
	{
		if(manager == null)
			manager = new EmbeddedPojoManager<Candle>(Candle.class);
		return manager;
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
