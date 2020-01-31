package scraper.api;

import java.time.Instant;
import java.util.ArrayList;

import scraper.entities.Candle;
import scraper.entities.Market;

public interface SourceConnector {
	public ArrayList<Market> getMarkets();
	public ArrayList<Candle> getCandles(String marketId, int granularity, Instant start, PullDirection direction);
}
