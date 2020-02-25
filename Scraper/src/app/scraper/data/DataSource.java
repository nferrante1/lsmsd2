package app.scraper.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;

import app.scraper.data.mongo.DBManager;

public class DataSource extends DataObject
{
	@SerializedName(value = "_id")
	@Expose
	protected String name;
	@Expose
	protected boolean enabled;
	@Expose
	protected List<Market> markets = new ArrayList<Market>();
	private transient boolean saved = true;
	
	@SuppressWarnings("unused")
	private DataSource()
	{
	}
	
	public DataSource(String name)
	{
		this.name = name;
		enabled = true;
		saved = false;
	}
	
	public static DataSource[] loadSources()
	{
		Gson gson = new Gson();
		List<Document> documents = DBManager.getInstance().find("Sources");
		List<DataSource> sources = new ArrayList<DataSource>();
		for (Document document: documents) {
			DataSource source = gson.fromJson(document.toJson(), DataSource.class);
			source.setAllMarketsSource();
			sources.add(source);
		}
		return sources.toArray(new DataSource[0]);
	}
	
	public void setAllMarketsSource()
	{
		for (Market market: markets)
			market.setSource(this);
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public Market getMarket(String marketId)
	{
		for (Market market: markets)
			if (market.getId().equals(marketId))
				return market;
		return null;
	}
	
	public List<Market> getMarkets()
	{
		return markets;
	}
	
	public void mergeMarkets(List<Market> markets)
	{
		if (!saved) {
			this.markets = markets;
			setAllMarketsSource();
			save();
			return;
		}
		Iterator<Market> savedMarketsIterator = this.markets.iterator();
		while (savedMarketsIterator.hasNext()) {
			Market savedMarket = savedMarketsIterator.next();
			Market upstreamMarket = null;
			Iterator<Market> upstreamMarketsIterator = markets.iterator();
			while (upstreamMarketsIterator.hasNext()) {
				Market market = upstreamMarketsIterator.next();
				if (market.getId().equals(savedMarket.getId())) {
					upstreamMarket = market;
					upstreamMarketsIterator.remove();
					break;
				}
			}
			if (upstreamMarket == null) {
				savedMarket.delete();
				savedMarketsIterator.remove();
				continue;
			}
			savedMarket.mergeWith(upstreamMarket);
			savedMarket.save();
		}
		List<Document> documents = new ArrayList<Document>();
		for (Market market: markets) {
			documents.add(market.getCreateDocument());
			market.setGranularity(5);
			this.markets.add(market);
		}
		DBManager.getInstance().updateOne("Sources", getFilter(), new Document("$push", new Document("markets", new Document("$each", documents))));
	}
	
	Bson getFilter()
	{
		return Filters.eq("_id", name);
	}
	
	Document getCreateDocument()
	{
		List<Document> marketsArray = new ArrayList<Document>();
		for (Market market: markets)
			marketsArray.add(market.getCreateDocument());
		return new Document()
			.append("_id", name)
			.append("enabled", enabled)
			.append("markets", marketsArray);
	}
	
	public void save()
	{
		if (!saved) {
			for (Market market: markets)
				market.setGranularity(5);
			Document document = getCreateDocument();
			DBManager.getInstance().insert("Sources", document);
			saved = true;
			return;
		}
		for (Market market: markets)
			market.save();
	}
}
