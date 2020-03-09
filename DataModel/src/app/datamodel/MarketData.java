package app.datamodel;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.DataObject;
import app.datamodel.mongo.DataObjectId;
import app.datamodel.mongo.Pojo;

@CollectionName("MarketData")
public class MarketData extends Pojo
{
	@DataObjectId
	@SerializedName(value = "_id")
	protected String id;
	protected List<Candle> candles = new ArrayList<Candle>();
	
	private MarketData() 
	{
		super();
	}
	
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
	
	public YearMonth getMonth()
	{
		return YearMonth.parse(id.split(":", 3)[2]);
	}
	
	public List<Candle> getCandles()
	{
		return this.candles;
	}
	
	/*@Override
	protected void postLoad() 
	{
		for(Candle candle : candles) 
			candle.setContainer(this);
	}*/
}
