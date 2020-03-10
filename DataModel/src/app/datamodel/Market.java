package app.datamodel;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import com.mongodb.client.model.Filters;

import app.datamodel.mongo.Embedded;
import app.datamodel.mongo.EmbeddedPojo;
import app.datamodel.mongo.EmbeddedPojoManager;

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
		return manager.find(Filters.eq("_id", sourceName), "id", true, pageNumber, perPage);
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
		MarketData.getManager().save(getContainer());
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
