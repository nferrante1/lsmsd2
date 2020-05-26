package app.scraper.net.data;

import com.google.gson.annotations.SerializedName;

public final class APIMarket
{
	@SerializedName(value = "id", alternate = "symbol")
	private String id;
	@SerializedName(value = "baseCurrency", alternate = { "base_currency", "baseAsset" })
	private String baseCurrency;
	@SerializedName(value = "quoteCurrency", alternate = { "quote_currency", "quoteAsset" })
	private String quoteCurrency;

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