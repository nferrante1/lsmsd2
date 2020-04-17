package app.datamodel;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.bson.BsonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.Candle;
import app.datamodel.pojos.DataRange;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.MarketData;
import app.datamodel.pojos.StorablePojoState;

public class MarketDataManager extends StorablePojoManager<MarketData>
{
	public MarketDataManager()
	{
		super(MarketData.class);
	}
	
	public int countLastCandles(String marketId)
	{
		MarketData market = find(generateFilter("market", marketId), getIncludeProjection("ncandles"), generateAscSort("start")).next();
		return (market == null) ? 1000 : market.getNcandles();
	}
	
	public void save(String marketId, List<Candle> candles)
	{
		int ncandles = candles.size();
		if (ncandles == 0)
			return;
		Bson update = ncandles == 1 ? Updates.push("candles", candles.get(0)) : Updates.pushEach("candles", candles);
		getCollection().updateOne(
				Filters.and(generateFilter("market", marketId), Filters.lte("ncandles", 1000 - ncandles)),
				Updates.combine(update, Updates.min("start", candles.get(0).getTime()), Updates.inc("ncandles", ncandles)),
				(new UpdateOptions()).upsert(true));
		for (Candle candle: candles)
			candle.commit();
	}
}
