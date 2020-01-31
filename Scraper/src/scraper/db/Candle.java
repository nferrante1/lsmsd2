package scraper.db;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Candle
{
	protected Market market;
	protected Instant time;
	protected double open;
	protected double high;
	protected double low;
	protected double close;
	protected double volume;
	protected int duration;
	
	
	private Candle()
	{
		
	}
	
	void setMarket(Market market)
	{
		this.market = market;
	}
	
	void setDuration(int duration)
	{
		this.duration = duration;
	}
	
	public Market getMarket()
	{
		return market;
	}
	
	public Instant getOpenTime()
	{
		return time;
	}
	
	public double getOpenPrice()
	{
		return open;
	}
	
	public double getHighPrice()
	{
		return high;
	}
	
	public double getLowPrice()
	{
		return low;
	}
	
	public double getClosePrice()
	{
		return close;
	}
	
	public double getVolume()
	{
		return volume;
	}
	
	public int getDuration()
	{
		return duration;
	}
	
	public Instant getCloseTime()
	{
		return getOpenTime().plusSeconds(getDuration());
	}
	
	public String getMonthString()
	{
		return ZonedDateTime.ofInstant(getOpenTime(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM"));
	}
}
