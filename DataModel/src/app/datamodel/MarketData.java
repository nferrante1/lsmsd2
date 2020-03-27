package app.datamodel;

import java.time.Instant;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Embedded;
import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

@CollectionName("MarketData")
public class MarketData extends Pojo
{
	@BsonId
	protected ObjectId id;
	protected String market;
	protected int ncandles;
	protected Instant start;
	@Embedded(value = MarketData.class, nestedName = "candles")
	protected List<Candle> candles = new ArrayList<Candle>();
		
	private MarketData() 
	{
		super();
	}
	
	public MarketData(String marketName, List<Candle> candles) 
	{
		super();
		this.market = marketName;
		this.start = candles.get(0).getTime();
		this.candles = candles;
		this.ncandles = candles.size();
	}

	public ObjectId getId()
	{
		return id;
	}

	public void setId(ObjectId id)
	{
		updateField("id", id);
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
