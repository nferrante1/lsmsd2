package app.scraper.net;

import java.time.Instant;
import java.util.List;

import app.scraper.data.Candle;
import app.scraper.data.Market;

public interface SourceConnector
{
	public List<Market> getMarkets() throws InterruptedException;
	public List<Candle> getCandles(String marketId, int granularity, Instant start, PullDirection direction) throws InterruptedException;
}
