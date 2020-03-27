package app.datamodel;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Embedded;
import app.datamodel.mongo.EmbeddedId;
import app.datamodel.mongo.EmbeddedPojo;
import app.datamodel.mongo.EmbeddedPojoManager;
import app.datamodel.mongo.PojoManager;

import org.bson.BsonNull;
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
	protected transient DataRange range;
	
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
	
	public DataRange getRange() {
		if(range == null) {
			range = new PojoManager<DataRange>(DataRange.class).aggregateOne(
					Arrays.asList(
							Aggregates.match(
									Filters.eq("market", "COINBASE:BTC-USD")), 
							Aggregates.sort(
									Sorts.ascending("start")), 
							Aggregates.group(new BsonNull(), 
									Accumulators.first(
											"start", "$start"), 
									Accumulators.last("last", 
											Filters.eq("$arrayElemAt", 
													Arrays.asList("$candles.t", -1L)
													)
											)
									)
							)
					);
		}
		return range;
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
