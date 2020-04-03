package app.datamodel;

import java.util.Arrays;

import org.bson.BsonNull;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.DataRange;
import app.datamodel.pojos.MarketData;

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

}
