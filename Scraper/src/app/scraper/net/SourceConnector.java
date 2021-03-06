package app.scraper.net;

import java.time.Instant;
import java.util.List;

import app.scraper.net.data.APICandle;
import app.scraper.net.data.APIMarket;

public interface SourceConnector
{
	public List<APIMarket> getMarkets() throws InterruptedException;

	public List<APICandle> getCandles(String marketId, int granularity, Instant start) throws InterruptedException;
}
