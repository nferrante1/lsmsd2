package scraper.db;

import java.time.Instant;

public class Market
{
	
	protected DataSource source;
	protected String id;
	protected String baseCurrency;
	protected String quoteCurrency;
	protected int granularity;
	protected boolean enabled;
	protected boolean streamingEnabled;
	protected MarketData data;
	protected Instant lastSavedCandleDate;
	
	private Market()
	{
		
	}
	
	public Market(String id)
	{
		this.id = id;
	}
	
	void setSource(DataSource source)
	{
		this.source = source;
	}
	
	protected void setGranularity(int granularity)
	{
		this.granularity = granularity;
	}
	
	protected void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	protected void setStreamingEnabled(boolean enabled)
	{
		this.streamingEnabled = enabled;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getFullSymbolName()
	{
		return getSourceName() + ":" + getSymbolName();
	}
	
	public String getSourceName()
	{
		return source.getName();
	}
	
	public String getSymbolName()
	{
		return getBaseCurrency() + "-" + getQuoteCurrency();
	}
	
	public String getBaseCurrency()
	{
		return baseCurrency;
	}
	
	public String getQuoteCurrency()
	{
		return quoteCurrency;
	}
	
	public int getGranularity()
	{
		return granularity;
	}
	
	public Instant getLastSavedCandleDate()
	{
		return lastSavedCandleDate;
	}
	
	public void downloadCandles()
	{
		
	}

}
