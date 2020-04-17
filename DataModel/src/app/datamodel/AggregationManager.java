package app.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import app.datamodel.mongo.DBManager;

public class AggregationManager {
	
	protected static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}
	
	protected static <T> MongoCollection<T> getCollection(String collectionName, Class<T> clazz)
	{
		return getDB().getCollection(collectionName, clazz);
	}
	
	//done aggregate SingleValueManager (?) - NOT USED
	public static <T> List<T> findMarketName(String marketName, int limit, int skip, boolean admin, Class<T> clazz) 
	{
		List<T> markets = new ArrayList<T>();
		
		List<Bson> projections = Arrays.asList(Projections.excludeId(), Projections.computed("value", Filters.eq("$concat", Arrays.asList("$_id", ":", "$markets.id"))), Projections.computed("granularity", "$markets.granularity"));
		
		if(admin)
			projections.addAll(Arrays.asList(Projections.computed("sync", "$markets.sync"), Projections.computed("selectable", "$markets.selectable")));
		List<Bson> stages = Arrays.asList(Aggregates.unwind("$markets"), Aggregates.match(Filters.regex("markets.id", marketName)), Aggregates.project(Projections.fields(projections)));
		if(skip != 0)
			stages.add(Aggregates.skip(skip));
		if(limit != 0)
			stages.add(Aggregates.limit(limit));
		AggregateIterable<T> aggregates =  getCollection("Sources", clazz).aggregate(stages);
		MongoCursor<T> cursor = aggregates.cursor();
		while(cursor.hasNext())
			markets.add(cursor.next());
		return markets;		
	}
	
	
}
