package scraper.db;

import java.util.ArrayList;

public class MarketData
{
	protected String id;
	protected Market market;
	protected String month;
	protected ArrayList<Candle> candles = new ArrayList<Candle>();
	
	public MarketData(Market market, int incrementalIndex, String month, Candle... candles)
	{
		this.id = market.getId() + ":" + incrementalIndex;
		this.market = market;
		this.month = month;
		for (Candle candle: candles)
			this.candles.add(candle);
	}
	
	public ArrayList<Candle> getCandles()
	{
		return candles;
	}
}
