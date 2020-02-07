package app.scraper.data;

import java.lang.reflect.Field;
import java.time.YearMonth;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;

import app.scraper.data.mongo.DBManager;

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
	protected transient Document setDocument;
	
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
	
	private void registerUpdate(String fieldName)
	{
		Field field;
		Object value;
		try {
			field = getClass().getDeclaredField(fieldName);
			value = field.get(this);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		if (setDocument == null)
			setDocument = new Document();
		setDocument.append("markets.$." + fieldName, value);
	}
	
	public void setBaseCurrency(String baseCurrency)
	{
		if (this.baseCurrency.equals(baseCurrency))
			return;
		this.baseCurrency = baseCurrency;
		registerUpdate("baseCurrency");
	}
	
	public void setQuoteCurrency(String quoteCurrency)
	{
		if (this.quoteCurrency.equals(quoteCurrency))
			return;
		this.quoteCurrency = quoteCurrency;
		registerUpdate("quoteCurrency");
	}
	
	public void setFilled(boolean filled)
	{
		if (this.filled == filled)
			return;
		this.filled = filled;
		registerUpdate("filled");
	}
	
	Bson getFilter(String sourceName)
	{
		return Filters.and(Filters.eq("_id", sourceName), Filters.eq("markets.id", getId()));
	}
	
	Document getCreateDocument()
	{
		return new Document()
			.append("id", id)
			.append("baseCurrency", baseCurrency)
			.append("quoteCurrency", quoteCurrency)
			.append("filled", filled);
	}
	
	public void save(String sourceName)
	{
		if (setDocument == null)
			return;
		DBManager.getInstance().updateOne("Sources", getFilter(sourceName), new Document("$set", setDocument));
		setDocument = null;
	}
	
	public void mergeWith(Market market)
	{
		setBaseCurrency(market.getBaseCurrency());
		setQuoteCurrency(market.getQuoteCurrency());
	}
	
	public void delete(String sourceName)
	{
		DBManager.getInstance().updateOne("Sources", getFilter(sourceName), new Document("$pull", new Document("markets", new Document("id", getId()))));
	}
}
