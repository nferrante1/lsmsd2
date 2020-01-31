package scraper.entities;

public class Market 
{
	protected String id;
	protected String baseCurrency;
	protected String quoteCurrency;
	protected int granularity;
	protected boolean enabled;
	protected boolean streamingEnabled;
	protected String firstDataMonth;
	private MarketData data;
	
	private Market() {}
	
	public void addCandles(Candle... candles) {}

	public int getGranularity()
	{
		return granularity;
	}

	protected void setGranularity(int granularity)
	{
		this.granularity = granularity;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isStreamingEnabled()
	{
		return streamingEnabled;
	}

	public void setStreamingEnabled(boolean streamingEnabled)
	{
		this.streamingEnabled = streamingEnabled;
	}

	public String getId()
	{
		return id;
	}

	public String getBaseCurrency()
	{
		return baseCurrency;
	}

	public String getQuoteCurrency()
	{
		return quoteCurrency;
	}

	public String getFirstDataMonth()
	{
		return firstDataMonth;
	}

	public MarketData getData()
	{
		return data;
	}
}
