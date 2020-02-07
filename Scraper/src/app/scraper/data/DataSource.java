package app.scraper.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
	@Expose // FIXME: must use arraylist
	protected HashMap<String, Market> markets = new HashMap<String, Market>();
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
		List<String> jsonDocuments = DBManager.getInstance().find("Sources");
		List<DataSource> sources = new ArrayList<DataSource>();
		for (String jsonDocument: jsonDocuments)
			sources.add(gson.fromJson(jsonDocument, DataSource.class));
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
	
	public void mergeMarkets(Market... markets)
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
		}
		DBManager.getInstance().insert("Sources", newMarketsJson);
	}
	
	public void mergeMarkets(List<Market> markets)
	{
		mergeMarkets(markets.toArray(new Market[0]));
	}
}
