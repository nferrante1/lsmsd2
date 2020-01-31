package scraper.sources;

import java.util.ArrayList;
import java.util.Map;

import scraper.db.Candle;
import scraper.db.Market;

public interface SourceConnector
{
	
	public ArrayList<Market> getMarkets();
	
	public ArrayList<Candle> getBars(String marketId, long start, int granularity);
	
}
