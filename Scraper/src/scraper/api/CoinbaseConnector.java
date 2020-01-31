package scraper.api;

import java.time.Instant;
import java.util.ArrayList;

import scraper.entities.Candle;
import scraper.entities.Market;

public class CoinbaseConnector implements SourceConnector {

	@Override
	public ArrayList<Market> getMarkets()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Candle> getCandles(String marketId, int granularity, Instant start, PullDirection direction)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
