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

public class MarketDataManager extends StorablePojoManager<MarketData>
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

	//TODO: maybe remove
	protected void aggregateCandles(String sourceName, String marketName, int granularity)
	{
		/*aggregate(
			Aggregates.match(Filters.eq("market", sourceName + ":" + marketName)),
			Aggregates.unwind("$candles"),
			Aggregates.addFields(new Field<Document>("bucket",
				new Document("$floor",
					new Document("$divide",
						Arrays.asList(
							new Document("$subtract",
								Arrays.asList("$candles.t", Instant.EPOCH)
								),
							granularity*60*1000)))
				)),
			Aggregates.group("$bucket",
				Accumulators.first("start", "$candles.t"),
				Accumulators.first("market", "$market"),
				Accumulators.first("t", "$candles.t"),
				Accumulators.first("o", "$candles.o"),
				Accumulators.max("h", "$candles.h"),
				Accumulators.min("l", "$candles.l"),
				Accumulators.last("c", "$candles.c"),
				Accumulators.sum("v", "$candles.v")
				),
			Aggregates.sort(Sorts.ascending("start")),
			Aggregates.project(Projections.fields(
				Projections.excludeId(),
				Projections.include("market"),
				Projections.computed("candle.t", "$t"),
				Projections.computed("candle.o", "$o"),
				Projections.computed("candle.h", "$h"),
				Projections.computed("candle.l", "$l"),
				Projections.computed("candle.c", "$c"),
				Projections.computed("candle.v", "$v")
				)),
			Aggregates.group(new BsonNull(),
				Accumulators.first("market", "$market"),
				Accumulators.push("candles", "$candle")
				),
			Aggregates.unwind("$candles", new UnwindOptions().includeArrayIndex("bucket")),
			Aggregates.addFields(new Field<Document>("bucket",
				new Document("$floor",
					new Document("$divide", Arrays.asList("$bucket", 1000)))
				)),
			Aggregates.group("$bucket",
				Accumulators.push("candles", "$candles"),
				Accumulators.first("market", "$market"),
				Accumulators.sum("ncandles", 1),
				Accumulators.first("start", "$candles.t")
				),
			Aggregates.project(Projections.excludeId()),
			Aggregates.merge("tmpMarketData", new MergeOptions().whenMatched(WhenMatched.REPLACE)))
			);*/
	}
}
