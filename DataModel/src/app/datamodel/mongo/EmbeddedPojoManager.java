package app.datamodel.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;

public class EmbeddedPojoManager<T extends EmbeddedPojo> extends PojoManager<T>
{
	protected String fieldName;
	
	public EmbeddedPojoManager(Class<T> pojoClass)
	{
		super(pojoClass);
		collectionName = Pojo.getCollectionName(EmbeddedPojo.getContainerClass(pojoClass));
		fieldName = EmbeddedPojo.getFieldName(pojoClass);
	}
	
	@Override
	public void insert(T pojo)
	{
		updateOne(pojo.getContainerFilter(), Updates.push(fieldName, pojo));
	}
	
	public void insert(List<T> pojos)
	{
		if (pojos.isEmpty())
			return;
		updateOne(pojos.get(0).getContainerFilter(), Updates.pushEach(fieldName, pojos));
	}
	
	public boolean update(T pojo)
	{
		return updateOne(pojo.getContainerFilter(), Updates.set(fieldName, pojo));
		
	}
	
	public boolean update(List<T> pojos)
	{
		if (pojos.isEmpty())
			return false;
		return updateOne(pojos.get(0).getContainerFilter(), Updates.combine(Updates.unset(fieldName), Updates.pushEach(fieldName, pojos)));
	}
	
	public boolean delete(T pojo)
	{
		if(pojo.isEmbeddedList())
			return updateOne(pojo.getContainerFilter(), Updates.pull(fieldName, pojo));
		else return updateOne(pojo.getContainerFilter(), Updates.unset(fieldName));
	}
	
	public boolean delete(List<T> pojos)
	{
		if(pojos.isEmpty()) return false;
		
		if(pojos.get(0).isEmbeddedList())
			return updateOne(pojos.get(0).getContainerFilter(), Updates.pull(fieldName, pojos));
		else return updateOne(pojos.get(0).getContainerFilter(), Updates.unset(fieldName));
	}
	
	public List<T> find(Bson filter, String sortField, boolean ascending, int skip, int limit)
	{
		List<T> pojos = new ArrayList<T>();
		MongoCursor<T> cursor;
		AggregateIterable<T> result;
		List<Bson> stages = new ArrayList<Bson>();
		stages.add(Aggregates.replaceRoot(fieldName));
		
		if(skip > 0) 
			stages.add(Aggregates.skip(skip));
		if(limit > 0) 
			stages.add(Aggregates.limit(limit));
		if(sortField != null)
			if(ascending)
				stages.add(Aggregates.sort(Sorts.ascending(sortField)));
			else
				stages.add(Aggregates.sort(Sorts.descending(sortField)));
		
		result = getCollection().aggregate(stages);
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
	
}
