package app.datamodel.pojos;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import app.datamodel.MarketDataManager;

import org.bson.BsonNull;
import org.bson.codecs.pojo.annotations.BsonIgnore;


public class Market extends Pojo
{	
	public String id;
	protected String baseCurrency;
	protected String quoteCurrency;
	protected int granularity;
	protected boolean selectable;
	protected boolean sync;
	protected transient DataRange range;
	protected transient int lastNCandles;
	
	public Market()
	{
		super();
	}

	public Market(String id, String base, String quote )
	{
		super(PojoState.STAGED);
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
	
	@BsonIgnore	
	public DataRange getRange() {
		if(range == null) {
			MarketDataManager marketDataManager = new MarketDataManager();
			range = marketDataManager.getRange(getId());
		}
		return range;
	}
	
	@BsonIgnore	
	public int getLastMarketDataCandles() {
		if(lastNCandles <= 0) {
			MarketDataManager marketDataManager = new MarketDataManager();
			lastNCandles = marketDataManager.lastMarketDataCandles(getId());
		}
		return lastNCandles;
	}
	
	
	public void setBaseCurrency(String baseCurrency)
	{
		updateField("baseCurrency", baseCurrency);
	}
	
	public void setQuoteCurrency(String quoteCurrency)
	{
		updateField("quoteCurrency", quoteCurrency);
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
}
