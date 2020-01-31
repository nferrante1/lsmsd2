package scraper.entities;

import java.time.Instant;

public class Candle {
	protected Instant time;
	protected double open;
	protected double high;
	protected double low;
	protected double close;
	protected double volume;
	
	private Candle() 
	{
		
	}
	
	public Candle(Instant t, double o, double h, double l, double c, double v) 
	{
		this.time = t;
		this.open = o;
		this.high = h;
		this.low = l;
		this.close = c;
		this.volume = v;
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
