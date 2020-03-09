package app.datamodel;

import java.lang.reflect.Field;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.DBManager;
import app.datamodel.mongo.DataObjectId;
import app.datamodel.mongo.Embedded;
import app.datamodel.mongo.EmbeddedPojo;
import app.datamodel.mongo.EmbeddedPojoManager;
import app.datamodel.mongo.NestedDataObject;
import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

@Embedded(value = DataSource.class, nestedName = "markets")
public class Market extends EmbeddedPojo
{
	protected String id;
	protected String baseCurrency;
	protected String quoteCurrency;
	protected int granularity;
	protected boolean selectable;
	protected boolean sync;
	protected boolean filled;
	protected transient MarketData data;
	private static transient EmbeddedPojoManager<Market> manager;
	
	private Market()
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
	
	public static List<Market> load(String sourceName, int pageNumber, int perPage)
	{
		return getManager().find(Filters.eq("_id", sourceName), "id", true, pageNumber, perPage);
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
	
	public YearMonth getFirstDataMonth() {
		return DataRangeCache.getInstance().getStartMonth(((DataSource)getContainer()).getName() + ":" + getId());

	}

	public YearMonth getLastDataMonth() {
		return DataRangeCache.getInstance().getEndMonth(((DataSource)getContainer()).getName() + ":" + getId());
	}
	
	
	
	public void addCandles(Candle... candles) 
	{
		if(data == null)
			data = new MarketData(((DataSource)getContainer()).getName(), getId(), YearMonth.from(candles[0].getTime().atZone(ZoneId.of("UTC")).toLocalDate()).toString());
		
		for(Candle candle: candles)
			data.addCandles(candle);
	}
		
	public void saveData() 
	{
		data.getManager().save();
		YearMonth newMonth = (data.getMonth() == null)? YearMonth.now() : data.getMonth();
		if (getLastDataMonth() == null || newMonth.isAfter(getLastDataMonth()))
			DataRangeCache.getInstance().setEndMonth(((DataSource)getContainer()).getName() + ":" + getId(), newMonth);
		if (getFirstDataMonth() == null || newMonth.isBefore(getFirstDataMonth()))
			DataRangeCache.getInstance().setStartMonth(((DataSource)getContainer()).getName() + ":" + getId(), newMonth);
		
		data = null;
	}


	protected static EmbeddedPojoManager<Market> getManager()
	{
		if (manager == null)
			manager = new EmbeddedPojoManager<Market>(Market.class);
		return manager;
	}
}
