package app.scraper;


import java.time.YearMonth;
import java.util.List;

import app.datamodel.Candle;
import app.datamodel.DataSource;
import app.datamodel.Market;
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
		
		source.save();
		
		if (!source.isEnabled())
		{
			System.out.println(getName() + ": Source not enabled. Exiting...");
			return;
		}
		
		List<Market> sourceMarkets = source.getMarkets();
		
		while(true) {
			for(Market market: sourceMarkets) {
				if (!market.isSyncEnabled())
					continue;
				YearMonth month = market.getLastDataMonth();
				if(month == null)
					month = YearMonth.now();
				else if (month.equals(YearMonth.now()))
					month = market.getFirstDataMonth().minusMonths(1);
				else
					month = month.plusMonths(1);
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
				market.saveData();
				Thread.yield();
			}
		}
		
		
		//System.out.println(getName() + ": Exiting...");
	}
}
