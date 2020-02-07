package app.scraper.data;

import java.time.YearMonth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Market
{
	@SerializedName(value = "id", alternate = "symbol")
	@Expose
	protected String id;
	@SerializedName(value = "baseCurrency", alternate = {"base_currency", "baseAsset"})
	@Expose
	protected String baseCurrency;
	@SerializedName(value = "quoteCurrency", alternate = {"quote_currency", "quoteAsset"})
	@Expose
	protected String quoteCurrency;
	@Expose(serialize = false)
	protected int granularity;
	@Expose(serialize = false)
	protected boolean selectable;
	@Expose(serialize = false)
	protected boolean sync;
	@Expose
	protected boolean filled;
	protected transient YearMonth firstDataMonth;
	protected transient YearMonth lastDataMonth;
	protected transient MarketData data;
	
	private Market()
	{
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
	
	public String getDisplayName()
	{
		return baseCurrency + "/" + quoteCurrency;
	}
	
	public int getGranularity()
	{
		return granularity;
	}
	
	public boolean isSelectabled()
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
}
