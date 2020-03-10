package app.datamodel;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;

import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

public class MarketData extends Pojo
{
	@BsonId
	protected String id;
	protected List<Candle> candles = new ArrayList<Candle>();
	private static transient PojoManager<MarketData> manager;
	
	private MarketData() 
	{
		super();
	}
	
	public MarketData(String sourceName, String marketName, String month) 
	{
		super();
		this.id = sourceName + ":" + marketName + ":" + month;
	}
	
	public static PojoManager<MarketData> getManager()
	{
		if (manager == null)
			manager = new PojoManager<MarketData>(MarketData.class);
		return manager;
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
