package app.datamodel.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import app.datamodel.DataSource;


public class PojoManager<T extends Pojo>
{
	protected final Class<T> pojoClass;
	protected String collectionName;
	
	public PojoManager(final Class<T> pojoClass)
	{
		this.pojoClass = pojoClass;
		this.collectionName = Pojo.getCollectionName(pojoClass);
	}
	
	protected static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}

	protected MongoCollection<T> getCollection()
	{
		return getDB().getCollection(collectionName, pojoClass);
	}

	public final void save(List<T> pojos)
	{
		for (T pojo: pojos) {
			if (pojo.isSaved()) {
				update(pojo);
				return;
			}
			insert(pojo);
		}
	}
	
	public void insert(List<T> pojos)
	{
		getCollection().insertMany(pojos);
		for (T pojo: pojos)
			pojo.setSaved();
	}
	
	public void insert(T pojo)
	{
		getCollection().insertOne(pojo);
		pojo.setSaved();
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
			while (cursor.hasNext()) {
				T pojo = cursor.next();
				pojo.setSaved();
				pojos.add(pojo);
			}
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
	
	public boolean update(T pojo)
	{
		return updateOne(pojo.getFilter(), pojo.getUpdateDocument());
	}
	
	public boolean updateOne(Bson filter, Bson update)
	{
		if(update == null) return false;
		UpdateResult updateResult = getCollection().updateOne(filter, update);
		return updateResult.wasAcknowledged();
	}
	
	public long updateMany(Bson filter, Bson update)
	{
		if(update == null) return 0;
		UpdateResult updateResult = getCollection().updateMany(filter, update);
		return updateResult.getModifiedCount();
	}
	
	public boolean delete(T pojo)
	{
		pojo.setSaved(false);
		return deleteOne(pojo.getFilter());
	}
	
	public boolean deleteOne(Bson filter)
	{
		DeleteResult deleteResult = getCollection().deleteOne(filter);
		return deleteResult.wasAcknowledged();
	}
	
	public long deleteMany(Bson filter)
	{
		if (filter == null) {
			drop();
			return 0;
		}
		DeleteResult deleteResult = getCollection().deleteMany(filter);
		return deleteResult.getDeletedCount();
	}
	
	public void drop()
	{
		getCollection().drop();
	}
	
	/*public boolean deleteEmbedded(EmbeddedPojo pojo)
	{
		return deleteEmbedded(pojo.getContainer().getFilter(), pojo.getFilter());
	}
	
	public boolean deleteEmbedded(Bson rootFilter, Bson filter)
	{
		return updateOne(rootFilter, Updates.pullByFilter(filter));
	}*/
	
	public boolean replace(T pojo)
	{
		return replace(pojo.getFilter(), pojo);
	}
	
	public boolean replace(Bson filter, T replacement)
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

	public void save(T pojo)
	{
		save(Arrays.asList(pojo));
	}
	
	//insertDocument( String collectionName, Document document){}
	
	/*private List<T> findEmbedded(Bson filter, String sortField, boolean ascending, int skip, int limit)
	{
		String subField = EmbeddedPojo.getFieldName(pojoClass);
		List<Bson> stages = new ArrayList<Bson>();
		if (filter != null)
			stages.add(Aggregates.match(filter));
		stages.add(Aggregates.unwind("$" + subField));
		stages.add(Aggregates.replaceRoot("$" + subField));
		if (sortField != null)
			stages.add(Aggregates.sort(ascending ? Sorts.ascending(sortField) : Sorts.descending(sortField)));
		if (skip > 0)
			stages.add(Aggregates.skip(skip));
		if (limit > 0)
			stages.add(Aggregates.limit(limit));
		return aggregate(stages);
	}*/
}
