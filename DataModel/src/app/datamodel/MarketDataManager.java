package app.datamodel;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import app.datamodel.pojos.Candle;
import app.datamodel.pojos.MarketData;

public final class MarketDataManager extends StorablePojoManager<MarketData>
{
	public MarketDataManager()
	{
		super(MarketData.class);
	}

	public int countLastCandles(String marketId)
	{
		MarketData market = find(Filters.eq("market", marketId),
			Projections.fields(
				Projections.excludeId(),
				Projections.include("ncandles")),
			Sorts.descending("start")).next();
		return (market == null) ? 0 : market.getNcandles();
	}

	public void save(String marketId, List<Candle> candles)
	{
		int ncandles = candles.size();
		if (ncandles == 0)
			return;
		Bson update = ncandles == 1 ? Updates.push("candles", candles.get(0)) : Updates.pushEach("candles", candles);
		getCollection().updateOne(
				Filters.and(Filters.eq("market", marketId), Filters.lte("ncandles", 1000 - ncandles)),
				Updates.combine(update, Updates.min("start", candles.get(0).getTime()), Updates.inc("ncandles", ncandles)),
				(new UpdateOptions()).upsert(true));
		for (Candle candle: candles)
			candle.commit();
	}

	protected void delete(String sourceName)
	{
		getCollection().deleteMany(Filters.regex("name", Pattern.compile("^" + sourceName + ":", Pattern.CASE_INSENSITIVE)));
	}

	public void delete(String sourceName, String marketName)
	{
		if (marketName == null) {
			delete(sourceName);
			return;
		}
		getCollection().deleteMany(Filters.eq("market", sourceName + ":" + marketName));
	}

	public void delete(String sourceName, String marketName, Instant date)
	{
		if (date == null) {
			delete(sourceName, marketName);
			return;
		}

		getCollection().deleteMany(Filters.and(
			Filters.eq("market", sourceName + ":" + marketName),
			Filters.lt("$expr", Arrays.asList(
					new Document("$arrayElemAt", Arrays.asList("$candles.t", -1)),
					date)
				)));
		getCollection().updateOne(Filters.and(
			Filters.eq("market", sourceName + ":" + marketName),
			Filters.lt("start", date)),
			Updates.combine(
				Updates.set("start", date),
				Updates.pull("candles", new Document("t", new Document("$lt", date)))));
	}
}
