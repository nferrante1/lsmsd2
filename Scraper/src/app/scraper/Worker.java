package app.scraper;

import java.time.Instant;
import java.util.List;

import app.scraper.data.Candle;
import app.scraper.data.DataSource;
import app.scraper.data.Market;
import app.scraper.net.PullDirection;
import app.scraper.net.SourceConnector;

final class Worker extends Thread
{
	private final DataSource source;
	private final SourceConnector connector;
	
	public Worker(DataSource source, SourceConnector connector)
	{
		this.source = source;
		this.connector = connector;
	}
	
	@Override
	public void run()
	{
		try {
			execute();
		} catch (InterruptedException e) {
			System.out.println(getName() + ": Interrupted! Exiting...");
		}
	}
	
	private void execute() throws InterruptedException
	{
		System.out.println(getName() + ": source=" + source.getName());
		List<Market> markets = connector.getMarkets();
		if (markets == null) {
			System.out.println(getName() + ": No markets! Exiting...");
			return;
		}
		source.mergeMarkets(markets);
		if (source.getName().equals("BINANCE")) {
			List<Market> savedMarkets = source.getMarkets();
			Market market = savedMarkets.get(5);
			List<Candle> candles = connector.getCandles(market.getId(), market.getGranularity(), Instant.now(), PullDirection.REVERSE);
			for (Candle candle: candles)
				System.out.println(getName() + ": CANDLE: t=" + candle.getTime() + "; o=" + candle.getOpen() + "; h=" + candle.getHigh() + "; l=" + candle.getLow() + "; c=" + candle.getClose() + "; v=" + candle.getVolume());
			System.out.println(getName() + ": CANDLES COUNT = " + candles.size());
		}
		System.out.println(getName() + ": Exiting...");
	}
}
