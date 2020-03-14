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

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Embedded;
import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

@CollectionName("MarketData")
public class MarketData extends Pojo
{
	@BsonId
	protected String id;
	@Embedded(value = MarketData.class, nestedName = "candles")
	protected List<Candle> candles = new ArrayList<Candle>();
	protected transient int granularity;
	
	private MarketData() 
	{
		super();
	}
	
	public MarketData(String sourceName, String marketName, YearMonth month, int granularity) 
	{
		super();
		this.id = sourceName + ":" + marketName + ":" + month.toString();
		this.granularity = granularity;
		
		for(Instant start = month.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC); start.isBefore(month.atEndOfMonth().atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC)); start.plusSeconds(60 * granularity)) 
		{
			candles.add(new Candle(start));
		}
	}
	
	public static Document createEmpty()
	{
		
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		updateField("id", id);
	}

	public void addCandles(Candle... candles) 
	{
		for(Candle candle: candles) {
			Instant t1 = candle.getTime();
			Instant t2 = this.candles.get(0).getTime();
			int index = (int)(t1.toEpochMilli() - t2.toEpochMilli()) / (granularity*60*1000);
			Candle currCandle = this.candles.get(index);
			currCandle.setTime(candle.getTime());
			currCandle.setHigh(candle.getHigh());
			currCandle.setClose(candle.getClose());
			currCandle.setLow(candle.getLow());
			currCandle.setOpen(candle.getOpen());
			currCandle.setVolume(candle.getVolume());
		}
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
