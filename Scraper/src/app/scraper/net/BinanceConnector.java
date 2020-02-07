package app.scraper.net;

import java.time.Instant;
import java.util.List;

import app.scraper.data.Candle;
import app.scraper.data.Market;

public class BinanceConnector implements SourceConnector
{

	@Override
	public List<Market> getMarkets()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Candle> getCandles(String marketId, int granularity, Instant start, PullDirection direction)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
