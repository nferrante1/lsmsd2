package app.scraper;


import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import app.datamodel.Candle;
import app.datamodel.DataSource;
import app.datamodel.Market;
import app.datamodel.MarketData;
import app.datamodel.mongo.PojoManager;
import app.scraper.data.DataRange;
import app.scraper.data.DataRangeCache;
import app.scraper.data.DataRangeManager;
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
				
				boolean updateCurrent = false;
				
				if (!market.isSyncEnabled())
					continue;
			
				YearMonth month = YearMonth.now();
				
//				if (DataRangeCache.getInstance().getRange(market.getId()) == null) { //Nuovo
//					DataRangeCache.getInstance().setEndMonth(market.getId(), month);
//					DataRangeCache.getInstance().setStartMonth(market.getId(), month);
//				}
//				else if(DataRangeCache.getInstance().getEndMonth(market.getId()).isBefore(YearMonth.now())) {//Dati recenti non scaricati
//					month = DataRangeCache.getInstance().getEndMonth(market.getId()).plusMonths(1);
//					DataRangeCache.getInstance().setEndMonth(market.getId(), month);
//				}
//				else if(DataRangeCache.getInstance().getStartMonth(market.getId()) == null) {//Dati recenti scaricati ma dati vecchi non scaricati
//					month = DataRangeCache.getInstance().getStartMonth(market.getId()).minusMonths(1);
//				}
//				else if(DataRangeCache.getInstance().getStartMonth(market.getId()).isBefore(YearMonth.now())) {
//					month = DataRangeCache.getInstance().getStartMonth(market.getId()).minusMonths(1);
//				}
//				else if(DataRangeCache.getInstance().getStartMonth(market.getId()).isBefore(YearMonth.now())) {
//					month = DataRangeCache.getInstance().getStartMonth(market.getId()).minusMonths(1);
//				}
					
				List<APICandle> sourceCandles = connector.getMonthCandles(market.getId(), market.getGranularity(), month);
				for(APICandle candle : sourceCandles)
					market.addCandles(new Candle(
							candle.getTime(), 
							candle.getOpen(), 
							candle.getHigh(), 
							candle.getLow(), 
							candle.getClose(), 
							candle.getVolume()
					));
				
				if(updateCurrent)
					if(marketDataManager.update(market.getData())) System.out.println("market: " + market.getId() + "last month updated");
				else
					marketDataManager.insert(market.getData());
				
				market.flushData();
				
				
				if (DataRangeCache.getInstance().getEndMonth(market.getId()) == null || month.isAfter(DataRangeCache.getInstance().getEndMonth(market.getId())))
					DataRangeCache.getInstance().setEndMonth(market.getId(), month);
				if (DataRangeCache.getInstance().getStartMonth(market.getId()) == null || month.isBefore(DataRangeCache.getInstance().getStartMonth(market.getId())))
					DataRangeCache.getInstance().setStartMonth(market.getId(), month);
				
				Thread.yield();
			}
		}
		
		
		//System.out.println(getName() + ": Exiting...");
	}
}
