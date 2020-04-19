package app.scraper;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import app.datamodel.DataRangeManager;
import app.datamodel.MarketDataManager;
import app.datamodel.StorablePojoManager;
import app.datamodel.pojos.Candle;
import app.datamodel.pojos.DataRange;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.Market;
import app.datamodel.pojos.MarketData;
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
			if(market == null) {
				source.addMarket(new Market(curMarket.getId(), curMarket.getBaseCurrency(), curMarket.getQuoteCurrency()));
			} else {
				//market.setId(curMarket.getId());
				market.setBaseCurrency(curMarket.getBaseCurrency());
				market.setQuoteCurrency(curMarket.getQuoteCurrency());
			}
			
		}

		List<Market> sourceMarkets = source.getMarkets();
			
		NextMarket: for(Market sm: sourceMarkets) 
		{
			for(APIMarket m: markets)
				if (sm.getId().equals(m.getId()))
					continue NextMarket;
			sm.delete();
		}
		
		
		StorablePojoManager<DataSource> manager = new StorablePojoManager<DataSource>(DataSource.class);
		manager.save(source);
		
		if (!source.isEnabled())
		{
			System.out.println(getName() + ": Source not enabled. Exiting...");
			return;
		}
		
		MarketDataManager marketDataManager = new MarketDataManager();
		DataRangeManager dataRangeManager = new DataRangeManager();
		
		int marketsCount = sourceMarkets.size();
		while(true) {
			int disabledCount = 0;
			for(Market market: sourceMarkets) {
				if (!market.isSyncEnabled()) {
					disabledCount++;
					if (disabledCount < marketsCount)
						continue;
					System.out.println(getName() + ": All markets disabled! Exiting...");
					return;
				}
				
				DataRange range = market.getRange();
				if (range == null) {
					range = dataRangeManager.get(source.getName() + ":" + market.getId());
					market.setRange(range);
				}
				Instant start = range.end;
				if (start != null)
					start = start.plusSeconds(market.getGranularity() * 60);

				List<APICandle> sourceCandles = connector.getCandles(market.getId(), market.getGranularity(), start);

				int lastCandlesCount = market.getLastCandlesCount();
				if (lastCandlesCount < 0) {
					lastCandlesCount = marketDataManager.countLastCandles(source.getName() + ":" + market.getId());
					market.setLastCandlesCount(lastCandlesCount);
				}
				
				if (sourceCandles.isEmpty())
					continue;

				List<Candle> candles = new ArrayList<Candle>();
				int sourceCandlesCount = sourceCandles.size();
				int toUpsert = lastCandlesCount == 0 ? 0 : 1000 - lastCandlesCount;
				int index = 0;
				for (toUpsert = Math.min(toUpsert, sourceCandlesCount); toUpsert > 0; toUpsert--) {
					APICandle c = sourceCandles.get(index);
					candles.add(new Candle(c.getTime(),c.getOpen(), c.getHigh(), c.getLow(), c.getClose(), c.getVolume()));
					index++;
				}
				marketDataManager.save(source.getName() + ":" + market.getId(), candles);

				List<MarketData> marketDatas = new ArrayList<MarketData>();
				int remainingCandles = sourceCandlesCount - index;
				while (remainingCandles > 0) {
					candles = new ArrayList<Candle>();
					for (int i = 0; i < Math.min(remainingCandles, 1000); i++) {
						APICandle c = sourceCandles.get(index + i );
						candles.add(new Candle(c.getTime(),c.getOpen(), c.getHigh(), c.getLow(), c.getClose(), c.getVolume()));
					}
					marketDatas.add(new MarketData(source.getName() + ":" + market.getId(), candles));
					remainingCandles -= candles.size();
				}
				marketDataManager.save(marketDatas);
				if(!marketDatas.isEmpty())
				{
					MarketData lastMarketData = marketDatas.get(marketDatas.size() - 1);
					market.setLastCandlesCount(lastMarketData.getNcandles());
					if(range.start == null)
						range.start = lastMarketData.getStart();
					range.end = lastMarketData.getEnd();
				}
				else
				{
					market.setLastCandlesCount(lastCandlesCount + index);
					if(range.start == null)
						range.start = candles.get(0).getTime();
					range.end = candles.get(candles.size() -1).getTime();
				}
				Thread.yield();
				
			}
			
			
		}
	}
}
