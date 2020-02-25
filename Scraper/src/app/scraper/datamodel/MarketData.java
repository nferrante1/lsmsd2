package app.scraper.datamodel;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import app.scraper.datamodel.mongo.CollectionName;
import app.scraper.datamodel.mongo.DataObject;

@CollectionName("MarketData")
public class MarketData extends DataObject
{
	@SerializedName(value = "_id")
	protected String id;
	protected List<Candle> candles = new ArrayList<Candle>();
	
	public MarketData(String sourceName, String marketName, String month) 
	{
		super();
		this.id = sourceName + ":" + marketName + ":" + month;
	}
	
	//public static createEmpty
	
	public void addCandles(Candle... candles) 
	{
		for(Candle candle: candles)
			this.candles.add(candle);
	}
}
