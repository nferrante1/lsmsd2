package app.datamodel;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.bson.BsonNull;
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
import app.datamodel.pojos.PojoState;

public class MarketDataManager {
	
	protected static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}

	protected MongoCollection<MarketData> getCollection()
	{
		return getDB().getCollection("MarketData", MarketData.class);
	}
	
	public DataRange getRange(String marketId)
	{
		
		MongoCursor<DataRange> range = getDataRangeCollection().aggregate(
				Arrays.asList(
						Aggregates.match(
								Filters.eq("market", marketId)), 
						Aggregates.sort(
								Sorts.ascending("start")), 
						Aggregates.group(new BsonNull(), 
								Accumulators.first(
										"start", "$start"), 
								Accumulators.last("last", 
										Filters.eq("$arrayElemAt", 
												Arrays.asList("$candles.t", -1L)
												)
										)
								)
						)
				).cursor();
	
		if(range.hasNext())
			return range.next();
		
		return new DataRange();
	}
	
	public MongoCollection<DataRange> getDataRangeCollection()
	{
		return getDB().getCollection("MarketData", DataRange.class);
	}
	
	public int lastMarketDataCandles(String marketId) {
		
		MarketData market = getCollection().find(Filters.eq("market", marketId)).sort(Sorts.descending("start")).projection(Projections.include("ncandles")).first();
		return (market == null)? 1000 : market.getNCandles();
	}
	
	public MarketData find(ObjectId marketDataId) 
	{
		return getCollection().find(Filters.eq("_id", marketDataId)).first();
		
	}
	
	public PojoCursor<MarketData> find(String marketId) 
	{
		FindIterable<MarketData> cursor = getCollection().find(Filters.eq("market", marketId)).sort(Sorts.ascending("start"));
		return new PojoCursor<MarketData>(cursor.cursor());
	}
	
	public void insert(MarketData marketData) 
	{
		getCollection().insertOne(marketData);
		marketData.setState(PojoState.COMMITTED);
	}
	
	public void insert(List<MarketData> marketDatas) 
	{
		getCollection().insertMany(marketDatas);
		for(MarketData marketData : marketDatas )
			marketData.setState(PojoState.COMMITTED);
	}
	
	public void insert(String marketId, Candle candle) {
		
		getCollection().updateOne(
				Filters.and(Filters.eq("market", marketId), Filters.lt("ncandles", 1000)), 
				Updates.combine(Updates.push("candles", candle), Updates.min("start", candle.getTime()), Updates.inc("ncandles", 1)), (new UpdateOptions()).upsert(true));
	}
	
	public void insert(String marketId, List<Candle> candles) {
		
		getCollection().updateOne(
				Filters.and(Filters.eq("market", marketId), Filters.lt("ncandles", 1000)), 
				Updates.combine(Updates.pushEach("candles", candles), Updates.min("start", candles.get(0).getTime()), Updates.inc("ncandles", candles.size())), (new UpdateOptions()).upsert(true));
	}
	
	public boolean delete(MarketData marketData)
	{
		marketData.setState(PojoState.REMOVED);
		return getCollection().deleteOne(Filters.eq("_id", marketData.getId())).wasAcknowledged();
	}
	
	public long delete(String marketId)
	{
		return getCollection().deleteMany(Filters.eq("market", marketId)).getDeletedCount();
	}
	
	public long delete(String marketId, Instant date)
	{
		return getCollection().deleteMany(Filters.and(Filters.eq("market", marketId), Filters.lte("start", date))).getDeletedCount();
	}
	
	
	
	

}
