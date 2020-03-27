package app.datamodel;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import com.mongodb.client.model.Filters;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Embedded;
import app.datamodel.mongo.EmbeddedId;
import app.datamodel.mongo.EmbeddedPojo;
import app.datamodel.mongo.EmbeddedPojoManager;
import app.datamodel.mongo.PojoManager;

import org.bson.codecs.pojo.annotations.BsonIgnore;

@CollectionName("Sources")
@Embedded(value = DataSource.class, nestedName = "markets", list=true)
public class Market extends EmbeddedPojo
{
	@EmbeddedId
	public String id;
	protected String baseCurrency;
	protected String quoteCurrency;
	protected int granularity;
	protected boolean selectable;
	protected boolean sync;
	protected boolean filled;
	protected transient MarketData data;
	
	public Market()
	{
		super();
	}

	public Market(String id, String base, String quote )
	{
		super();
		this.id = id;
		this.baseCurrency = base;
		this.quoteCurrency = quote;
		this.granularity = 5;		
	}
	

	public String getId()
	{
		return id;
	}
	
	public String getBaseCurrency()
	{
		return baseCurrency;
	}
	
	public String getQuoteCurrency()
	{
		return quoteCurrency;
	}
	
	public int getGranularity()
	{
		return granularity;
	}
	
	
	public boolean isSelectable()
	{
		return selectable;
	}
	
	@BsonIgnore
	public boolean isSyncEnabled()
	{
		return sync;
	}
	
	public boolean isFilled()
	{
		return filled;
	}
	
	public void setBaseCurrency(String baseCurrency)
	{
		updateField("baseCurrency", baseCurrency);
	}
	
	public void setQuoteCurrency(String quoteCurrency)
	{
		updateField("quoteCurrency", quoteCurrency);
	}
	
	public void setFilled(boolean filled)
	{
		updateField("filled", filled);
	}
	
	public void addCandles(Candle... candles) 
	{
		//if(data == null)
			//data = new MarketData(((DataSource)getContainer()).getName(), getId(), YearMonth.from(candles[0].getTime().atZone(ZoneId.of("UTC")).toLocalDate()), granularity);
			//CHIAMARE POJOMANAGER
		//for(Candle candle: candles)
			//data.addCandles(candle);
			//CHIAMARE POJOMANAGER
	}
	
	public boolean isSync()
	{
		return sync;
	}

	public void setSync(boolean sync)
	{
		updateField("sync", sync);
	}

	public void setId(String id)
	{
		updateField("id", id);
	}

	public void setGranularity(int granularity)
	{
		updateField("granularity", granularity);
	}

	public void setSelectable(boolean selectable)
	{
		updateField("selectable", selectable);
	}

	public MarketData getData()
	{
		return this.data;
	}

	public void flushData()
	{
		this.data = null;
		
	}
}
