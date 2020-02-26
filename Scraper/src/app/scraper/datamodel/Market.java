package app.scraper.datamodel;

import java.lang.reflect.Field;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;
import app.scraper.datamodel.mongo.CollectionName;
import app.scraper.datamodel.mongo.DBManager;
import app.scraper.datamodel.mongo.NestedDataObject;

@CollectionName("Sources")
public class Market extends NestedDataObject
{
	@SerializedName(value = "id", alternate = "symbol")
	protected String id;
	@SerializedName(value = "baseCurrency", alternate = {"base_currency", "baseAsset"})
	protected String baseCurrency;
	@SerializedName(value = "quoteCurrency", alternate = {"quote_currency", "quoteAsset"})
	protected String quoteCurrency;
	protected int granularity;
	protected boolean selectable;
	protected boolean sync;
	protected boolean filled;
	protected transient YearMonth firstDataMonth;
	protected transient YearMonth lastDataMonth;
	protected transient MarketData data;
	
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
		if(firstDataMonth == null)
			getAvailableDataRange();
		return firstDataMonth;
	}

	public YearMonth getLastDataMonth() {
		if(lastDataMonth == null)
			getAvailableDataRange();
		return lastDataMonth;
	}
	
	protected void getAvailableDataRange() {
		List<Document> documents = aggregate("marketData", Arrays.asList(
			new Document("$match", new Document("_id","/^"+((DataSource)getContainer()).getName()+":"+getId()+":/")),
			new Document("$project", new Document("_id", 1)),
			new Document("$sort", new Document("_id", 1)),
			new Document("$group", new Document("_id", null).append("first", new Document("$first", "$$ROOT")).
									append("last", new Document("$last", "$$ROOT")))
			));
		if(documents.isEmpty())
			return;
		
		String firstId = documents.get(0).getEmbedded(Arrays.asList("first", "_id"), String.class);
		String lastId = documents.get(0).getEmbedded(Arrays.asList("last","_id"), String.class);
		firstDataMonth = YearMonth.parse(firstId.split(":", 3)[2]);
		lastDataMonth = YearMonth.parse(lastId.split(":", 3)[2]);
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
		data.save();
		YearMonth newMonth = data.getMonth();
		if (lastDataMonth == null || newMonth.isAfter(lastDataMonth))
			lastDataMonth = newMonth;
		if (firstDataMonth == null || newMonth.isBefore(firstDataMonth))
			firstDataMonth = newMonth;
		data = null;
	}
}
