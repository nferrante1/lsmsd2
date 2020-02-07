package app.scraper;

import java.util.List;

import app.scraper.data.DataSource;
import app.scraper.data.Market;
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
		System.out.println(getName() + ": source=" + source.getName());
		List<Market> markets = connector.getMarkets();
		if (markets == null) {
			System.out.println(getName() + ": No markets! Exiting...");
			return;
		}
		for (Market market: markets)
			System.out.println(getName() + ": id=" + market.getId() + "; displayName=" + market.getDisplayName());
		source.mergeMarkets(markets);
		System.out.println(getName() + ": Exiting...");
	}
}
