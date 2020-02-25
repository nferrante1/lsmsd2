package app.scraper.net.data;

public class APIMarket
{
	protected String id;
	protected String baseCurrency;
	protected String quoteCurrency;
	
	private APIMarket()
	{
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
}
