package app.common.net.entities;

public class MarketInfo extends Entity
{
	private static final long serialVersionUID = 4212262098735608977L;

	protected String sourceName;
	protected String marketId;
	protected String baseCurrency;
	protected String quoteCurrency;
	protected int granularity;
	protected boolean sync;
	protected boolean selectable;

	public MarketInfo()
	{
	}

	public MarketInfo(String sourceName, String marketId, int granularity, boolean sync, boolean enabled)
	{
		this.sourceName = sourceName;
		this.marketId = marketId;
		this.granularity = granularity;
		this.sync = sync;
		this.selectable = enabled;
	}

	public String getMarketDisplayName()
	{
		return getBaseCurrency() + "/" + getQuoteCurrency();
	}

	public String getDisplayName()
	{
		return getSourceName() + ":" + getMarketDisplayName();
	}

	public String getSourceName()
	{
		return sourceName;
	}

	public void setSourceName(String sourceName)
	{
		this.sourceName = sourceName;
	}

	public String getFullId()
	{
		return getSourceName() + ":" + getMarketId();
	}

	public String getMarketId()
	{
		return marketId;
	}

	public void setMarketId(String marketId)
	{
		this.marketId = marketId;
	}

	public String getBaseCurrency()
	{
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency)
	{
		this.baseCurrency = baseCurrency;
	}

	public String getQuoteCurrency()
	{
		return quoteCurrency;
	}

	public void setQuoteCurrency(String quoteCurrency)
	{
		this.quoteCurrency = quoteCurrency;
	}

	public int getGranularity()
	{
		return granularity;
	}

	public void setGranularity(int granularity)
	{
		this.granularity = granularity;
	}

	public boolean isSync()
	{
		return sync;
	}

	public void setSync(boolean sync)
	{
		this.sync = sync;
	}

	public boolean isSelectable()
	{
		return selectable;
	}

	public void setSelectable(boolean enabled)
	{
		this.selectable = enabled;
	}
}
