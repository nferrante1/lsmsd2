package app.datamodel.mongo;

import java.lang.reflect.ParameterizedType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class POJOManager<T extends Pojo>
{
	private final Class<T> pojoClass;
	private final String collectionName;
	
	public POJOManager(final Class<T> pojoClass)
	{
		this.pojoClass = pojoClass;
		this.collectionName = Pojo.getCollectionName(pojoClass);
	}
	
	private static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}

	private MongoCollection<T> getCollection()
	{
		return getDB().getCollection(collectionName, pojoClass);
	}
	
	public void insert(List<T> pojos)
	{
		getCollection().insertMany(pojos);
	}
	
	public void insert(String collectionName, T pojo)
	{
		getCollection().insertOne(pojo);
	}
	
	public List<T> find(Bson filter, String sortField, boolean ascending, int skip, int limit)
	{
		List<T> pojos = new ArrayList<T>();
		MongoCursor<T> cursor;
		FindIterable<T> result;
		if (filter == null)
			result = getCollection().find();
		else
			result = getCollection().find(filter);
		
		if(skip > 0) 
			result = result.skip(skip);
		if(limit > 0) 
			result = result.limit(limit);
		if(sortField != null)
			if(ascending)
				result = result.sort(Sorts.ascending(sortField));
			else
				result = result.sort(Sorts.descending(sortField));
		
		cursor = result.cursor();
		
		try {
			while (cursor.hasNext())
				pojos.add(cursor.next());
		} finally {
			cursor.close();
		}
		return pojos;
	}
	
	public List<T> find(Bson filter, String sortField, boolean ascending)
	{
		return find(filter, sortField, ascending, 0, 0);
	}
	public List<T> find(Bson filter, String sortField)
	{
		return find(filter, sortField, true);
	}
	
	public List<T> find(Bson filter)
	{
		return find(filter, null);
	}
	
	public List<T> find()
	{
		return find(null);
	}
	
	public long update(Bson filter, Document update)
	{
		UpdateResult updateResult = getCollection().updateMany(filter, update);
		return updateResult.getModifiedCount();
	}
	
	public long delete(Bson filter)
	{
		DeleteResult deleteResult = getCollection().deleteMany(filter);
		return deleteResult.getDeletedCount();
	}
	
	public boolean updateOne(Bson filter, Bson update)
	{
		UpdateResult updateResult = getCollection().updateOne(filter, update);
		return updateResult.wasAcknowledged();
	}
	
	public boolean deleteOne(Bson filter)
	{
		DeleteResult deleteResult = getCollection().deleteOne(filter);
		return deleteResult.wasAcknowledged();
	}
	
	public boolean replaceOne(Bson filter, T replacement)
	{
		UpdateResult updateResult = getCollection().replaceOne(filter, replacement);
		return updateResult.wasAcknowledged();
	}
	public List<T> aggregate(List<Bson> stages)
	{
		List<T> pojos = new ArrayList<T>();
		AggregateIterable<T> aggregateResult = getCollection().aggregate(stages);
		for(T pojo: aggregateResult)
			pojos.add(pojo);
		return pojos;
	}
	
	public List<T> findEmbedded(Bson filter, String subField, String sortField, boolean ascending, int skip, int limit)
	{
		List<Bson> stages = new ArrayList<Bson>();
		stages.add(Aggregates.match(filter));
		stages.add(Aggregates.unwind("$" + subField));
		stages.add(Aggregates.replaceRoot("$markets"));
		if (subField != null)
			stages.add(Aggregates.sort(ascending ? Sorts.ascending(sortField) : Sorts.descending(sortField)));
		if (skip > 0)
			stages.add(Aggregates.skip(skip));
		if (limit > 0)
			stages.add(Aggregates.limit(limit));
		return aggregate(stages);
	}
	
	public List<T> findEmbedded(Bson filter, String subField)
	{
		return findEmbedded(filter, subField, null);
	}

	public List<T> findEmbedded(Bson filter, String subField, String sortField)
	{
		return findEmbedded(filter, subField, sortField, true);
	}

	public List<T> findEmbedded(Bson filter, String subField, String sortField, boolean ascending)
	{
		return findEmbedded(filter, subField, sortField, ascending, 0, 0);
	}

	public List<T> findEmbedded(Bson filter, String subField, int skip, int limit)
	{
		return findEmbedded(filter, subField, null, false, skip, limit);
	}
}
