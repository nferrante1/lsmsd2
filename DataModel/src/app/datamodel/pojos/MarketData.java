package app.datamodel.pojos;

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


public class MarketData extends Pojo
{
	@BsonId
	protected ObjectId id;
	protected String market;
	protected int ncandles;
	protected Instant start;
	protected List<Candle> candles = new ArrayList<Candle>();
		
	public MarketData() 
	{
		super();
	}
	
	public MarketData(String marketName, List<Candle> candles) 
	{
		super(PojoState.STAGED);
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
	
	public int getNcandles() 
	{
		return this.ncandles;
	}
	
	public String getMarket() 
	{
		return market;
	}

	public void setMarket(String market) 
	{
		updateField("market", market);
	}

	public void setNcandles(int ncandles) 
	{
		updateField("ncandles", ncandles);
	}

	public Instant getStart() 
	{
		return start;
	}

	public void setStart(Instant start) 
	{
		updateField("start", start);
	}

	public void setCandles(List<Candle> candles) 
	{
		this.candles = candles;
	}
}
