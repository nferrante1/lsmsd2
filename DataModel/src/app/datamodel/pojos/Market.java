package app.datamodel.pojos;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.annotations.PojoId;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("Sources")
public class Market extends StorablePojo
{
	@PojoId
	public String id;
	protected String baseCurrency;
	protected String quoteCurrency;
	protected int granularity;
	protected boolean selectable;
	protected boolean sync;
	protected transient DataRange range;
	protected transient int lastCandlesCount = -1;

	public Market()
	{
		super();
	}

	public Market(String id, String base, String quote)
	{
		super(StorablePojoState.UNTRACKED);
		this.id = id;
		this.baseCurrency = base;
		this.quoteCurrency = quote;
		this.granularity = 5;
	}

	public String getId()
	{
		return id;
	}

	@BsonIgnore
	public String getMarketName()
	{
		return getBaseCurrency() + "/" + getQuoteCurrency();
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
	public DataRange getRange()
	{
		return range;
	}

	@BsonIgnore
	public int getLastCandlesCount()
	{
		return lastCandlesCount;
	}

	@BsonIgnore
	public void setRange(DataRange range)
	{
		this.range = range;
	}

	@BsonIgnore
	public void setLastCandlesCount(int lastCandlesCount)
	{
		this.lastCandlesCount = lastCandlesCount;
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
