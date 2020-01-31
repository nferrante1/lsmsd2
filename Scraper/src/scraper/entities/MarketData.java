package scraper.entities;

import java.util.ArrayList;

public class MarketData 
{
	protected String id;
	protected ArrayList<Candle> candles;
	
	public MarketData(String sourceName, String marketName, String month) 
	{
		this.id = sourceName + ":" + marketName + ":" + month;
	}
	
	public static MarketData createEmpty(String sourceName, String marketName, String month) 
	{
		return new MarketData(sourceName, marketName, month);
	}
	
	public String getMonth() 
	{
		return id.split(":",3)[2];
	}
	
	public void addCandles(Candle... candles) 
	{
		
	}
	
	public void save()
	{
		
	}
	
}
