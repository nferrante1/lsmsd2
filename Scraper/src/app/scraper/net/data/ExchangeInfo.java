package app.scraper.net.data;

import java.util.List;

public final class ExchangeInfo
{
	private List<APIMarket> symbols;

	public List<APIMarket> getMarkets()
	{
		return symbols;
	}
}
