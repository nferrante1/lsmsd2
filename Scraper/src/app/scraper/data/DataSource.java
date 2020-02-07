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

public class DataSource
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
		for (Document document: documents)
			sources.add(gson.fromJson(document.toJson(), DataSource.class));
		return sources.toArray(new DataSource[0]);
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
	
	public void mergeMarkets(List<Market> markets)
	{
		if (!saved) {
			this.markets = markets;
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
				savedMarket.delete(name);
				savedMarketsIterator.remove();
				continue;
			}
			savedMarket.mergeWith(upstreamMarket);
			savedMarket.save(name);
		}
		List<Document> documents = new ArrayList<Document>();
		for (Market market: markets)
			documents.add(market.getCreateDocument());
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
			Document document = getCreateDocument();
			DBManager.getInstance().insert("Sources", document);
			saved = true;
			return;
		}
		for (Market market: markets)
			market.save(name);
	}
	
	/*public void mergeMarkets(Market... markets)
	{ // FIXME: various bugs...
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		if (!saved) {
			for (Market market: markets)
				this.markets.put(market.getId(), market);
			DBManager.getInstance().insert("Sources", gson.toJson(this));
			return;
		}
		List<String> newMarketsJson = new ArrayList<String>();
		for (Market upstreamMarket: markets) {
			Market savedMarket = this.markets.get(upstreamMarket.getId());
			if (savedMarket == null) {
				this.markets.put(upstreamMarket.getId(), upstreamMarket);
				newMarketsJson.add(gson.toJson(upstreamMarket));
				continue;
			}
			Bson filter = Filters.and(Filters.eq("_id", name), Filters.eq("markets.id", savedMarket.getId()));
			Document setDocument = new Document();
			if (savedMarket.getBaseCurrency() != upstreamMarket.getBaseCurrency())
				setDocument.append("markets.$.baseCurrency", upstreamMarket.getBaseCurrency());
			if (savedMarket.getQuoteCurrency() != upstreamMarket.getQuoteCurrency())
				setDocument.append("markets.$.quoteCurrency", upstreamMarket.getQuoteCurrency());
			DBManager.getInstance().updateOne("Sources", filter, new Document("$set", setDocument));
		}.
		DBManager.getInstance().insert("Sources", newMarketsJson);
	}*/
}
