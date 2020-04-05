package app.scraper;


import java.time.Instant;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import app.datamodel.MarketDataManager;
import app.datamodel.SourcesManager;
import app.datamodel.pojos.Candle;
import app.datamodel.pojos.DataRange;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.Market;
import app.datamodel.pojos.MarketData;
import app.datamodel.pojos.PojoState;
import app.scraper.net.SourceConnector;
import app.scraper.net.data.APICandle;
import app.scraper.net.data.APIMarket;

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
		List<APIMarket> markets = connector.getMarkets();
		if (markets == null) {
			System.out.println(getName() + ": No markets! Exiting...");
			return;
		}
		
		for(APIMarket curMarket: markets) 
		{
			Market market = source.getMarket(curMarket.getId());
			if(market == null) 
				source.addMarket(new Market(curMarket.getId(), curMarket.getBaseCurrency(), curMarket.getQuoteCurrency()));
			else {
				market.setBaseCurrency(curMarket.getBaseCurrency());
				market.setQuoteCurrency(curMarket.getQuoteCurrency());
			}
			
		}
			
		OuterLoop: for(Market sm : source.getMarkets()) 
		{
			for(APIMarket m: markets) {
				if (sm.getId().equals(m.getId()))
					continue OuterLoop;
			}
			sm.setState(PojoState.REMOVED);
		}
		
		
		SourcesManager manager = new SourcesManager();
		manager.save(source);
		
		if (!source.isEnabled())
		{
			System.out.println(getName() + ": Source not enabled. Exiting...");
			return;
		}
		
		List<Market> sourceMarkets = source.getMarkets();
		
		MarketDataManager marketDataManager = new MarketDataManager();
		
		while(true) 
		{
			for(Market market: sourceMarkets) {				
				if (!market.isSyncEnabled())
					continue;
				
				DataRange range = market.getRange();
				Instant start = range.end;
				int ncandles = market.getLastMarketDataCandles();
				
				List<APICandle> sourceCandles = connector.getThousandCandles(market.getId(), market.getGranularity(), start, 1000-ncandles);
				List<Candle> candles = new ArrayList<Candle>();
				for(APICandle c : sourceCandles) 
					candles.add(new Candle(c.getTime(),c.getOpen(), c.getHigh(), c.getLow(), c.getClose(), c.getVolume()));
			
				if(ncandles == 1000) 
				{
					marketDataManager.insert(new MarketData(market.getId(), candles));
				}
				else
				{
					marketDataManager.insert(market.getId(), candles);
				}
				
				Thread.yield();
				
			}
			
			
		}
	}
}
