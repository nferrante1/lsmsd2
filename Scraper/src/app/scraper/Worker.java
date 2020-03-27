package app.scraper;


import java.time.Instant;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import app.datamodel.Candle;
import app.datamodel.DataRange;
import app.datamodel.DataSource;
import app.datamodel.Market;
import app.datamodel.MarketData;
import app.datamodel.mongo.PojoManager;

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
			else
				source.updateMarket(new Market(curMarket.getId(), curMarket.getBaseCurrency(), curMarket.getQuoteCurrency()));
		}
		
		List<Market> sourceMarket = new ArrayList<Market>(source.getMarkets());
		
		OuterLoop: for(Market sm : sourceMarket) 
		{
			for(APIMarket m: markets) {
				if (sm.getId().equals(m.getId()))
					continue OuterLoop;
			}
			source.removeMarket(sm.getId());
		}
		
		
		PojoManager<DataSource> manager = new PojoManager<DataSource>(DataSource.class);
		manager.save(source);
		
		if (!source.isEnabled())
		{
			System.out.println(getName() + ": Source not enabled. Exiting...");
			return;
		}
		
		List<Market> sourceMarkets = source.getMarkets();
		
		PojoManager<MarketData> marketDataManager = new PojoManager<MarketData>(MarketData.class);
		
		while(true) {
			for(Market market: sourceMarkets) {				
				if (!market.isSyncEnabled())
					continue;
			
				DataRange range = market.getRange();
				Instant start;
				if(range == null) 
					start = Instant.now();
				else if(market.isFilled())
					start = range.end;
				else 
					start = range.start;
				List<APICandle> sourceCandles = connector.getThousandCandles(market.getId(), market.getGranularity(), start, market.isFilled());
				List<Candle> candles = new ArrayList<Candle>();
				for(APICandle candle : sourceCandles)
					candles.add(new Candle(
							candle.getTime(), 
							candle.getOpen(), 
							candle.getHigh(), 
							candle.getLow(), 
							candle.getClose(), 
							candle.getVolume()
					));
				
				MarketData marketData = new MarketData(market.getId(), candles);
//				if(month.equals(YearMonth.now()))
//					marketDataManager.update(market.getData());
//				else
				marketDataManager.insert(marketData);
				
				market.flushData();
				
				Thread.yield();
			}
		}
		
		
		//System.out.println(getName() + ": Exiting...");
	}
}
