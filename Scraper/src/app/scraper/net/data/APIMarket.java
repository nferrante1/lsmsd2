package app.scraper.net.data;

import com.google.gson.annotations.SerializedName;

public class APIMarket
{
	@SerializedName(value = "id", alternate = "symbol")
	protected String id;
	@SerializedName(value = "baseCurrency", alternate = { "base_currency", "baseAsset" })
	protected String baseCurrency;
	@SerializedName(value = "quoteCurrency", alternate = { "quote_currency", "quoteAsset" })
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