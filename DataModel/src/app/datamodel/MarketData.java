package app.datamodel;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

@CollectionName("MarketData")
public class MarketData extends Pojo
{
	@BsonId
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
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void addCandles(Candle... candles) 
	{
		for(Candle candle: candles)
			this.candles.add(candle);
	}
	
	@BsonIgnore
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
