package app.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.annotations.CollectionName;

public class PojoManager<T extends Object>
{
	protected final Class<T> pojoClass;
	protected String collectionName;

	public PojoManager(Class<T> pojoClass)
	{
		this.pojoClass = pojoClass;
	}

	public PojoManager(Class<T> pojoClass, String collectionName)
	{
		this(pojoClass);
		this.collectionName = collectionName;
	}

	protected static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}

	public String getCollectionName()
	{
		if (collectionName == null)
			collectionName = pojoClass.isAnnotationPresent(CollectionName.class)
				? pojoClass.getAnnotation(CollectionName.class).value()
				: pojoClass.getSimpleName();
		return collectionName;
	}

	public void renameCollection(String collectionName)
	{
		getCollection().renameCollection(new MongoNamespace(collectionName));
		this.collectionName = collectionName;
	}

	public void replaceCollection(String newCollectionName)
	{
		getDB().getCollection(newCollectionName).drop();
		renameCollection(newCollectionName);
	}

	protected static <X> MongoCollection<X> getCollection(String name, Class<X> clazz)
	{
		return getCollection(name, clazz, null, null, null);
	}

	protected static <X> MongoCollection<X> getCollection(String name, Class<X> clazz,
		ReadPreference readPreference, ReadConcern readConcern, WriteConcern writeConcern)
	{
		if (readPreference == null)
			readPreference = DBManager.getReadPreference();
		if (readConcern == null)
			readConcern = DBManager.getReadConcern();
		if (writeConcern == null)
			writeConcern = DBManager.getWriteConcern();
		return getDB().getCollection(name, clazz).withReadPreference(readPreference)
			.withReadConcern(readConcern).withWriteConcern(writeConcern);
	}

	protected MongoCollection<T> getCollection(ReadPreference readPreference, ReadConcern readConcern,
		WriteConcern writeConcern)
	{
		return getCollection(getCollectionName(), pojoClass, readPreference, readConcern, writeConcern);
	}

	protected MongoCollection<T> getCollection(ReadConcern readConcern, WriteConcern writeConcern)
	{
		return getCollection(null, readConcern, writeConcern);
	}

	protected MongoCollection<T> getCollection(ReadPreference readPreference, ReadConcern readConcern)
	{
		return getCollection(readPreference, readConcern, null);
	}

	protected MongoCollection<T> getCollection(ReadPreference readPreference, WriteConcern writeConcern)
	{
		return getCollection(readPreference, null, writeConcern);
	}

	protected MongoCollection<T> getCollection(ReadConcern readConcern)
	{
		return getCollection(null, readConcern, null);
	}

	protected MongoCollection<T> getCollection(WriteConcern writeConcern)
	{
		return getCollection(null, null, writeConcern);
	}

	protected MongoCollection<T> getCollection(ReadPreference readPreference)
	{
		return getCollection(readPreference, null, null);
	}

	protected MongoCollection<T> getCollection()
	{
		return getCollection(null, null, null);
	}

	protected FindIterable<T> getFindIterable(Bson filter, Bson projection, Bson sort, int skip, int limit)
	{
		FindIterable<T> iterable;
		if (filter == null)
			iterable = getCollection().find();
		else
			iterable = getCollection().find(filter);
		if (projection != null)
			iterable = iterable.projection(projection);
		if (sort != null)
			iterable = iterable.sort(sort);
		if (skip > 0)
			iterable = iterable.skip(skip);
		if (limit > 0)
			iterable = iterable.limit(limit);
		return iterable;
	}

	public PojoCursor<T> find(Bson filter, Bson projection, Bson sort, int skip, int limit)
	{
		return new PojoCursor<T>(getFindIterable(filter, projection, sort, skip, limit).cursor());
	}

	public PojoCursor<T> find(Bson filter, Bson sort, int skip, int limit)
	{
		return find(filter, null, sort, skip, limit);
	}

	public PojoCursor<T> find(Object id, Bson sort, int skip, int limit)
	{
		return find(Filters.eq("_id", id), sort, skip, limit);
	}

	public PojoCursor<T> find(Object id, Bson projection, Bson sort)
	{
		return find(Filters.eq("_id", id), projection, sort);
	}

	public PojoCursor<T> find(Bson filter, Bson projection, Bson sort)
	{
		return find(filter, projection, sort, 0, 0);
	}

	public PojoCursor<T> find(Object id, Bson projection, Bson sort, int skip, int limit)
	{
		return find(Filters.eq("_id", id), projection, sort, skip, limit);
	}

	public PojoCursor<T> find(Bson filter, int skip, int limit)
	{
		return find(filter, null, skip, limit);
	}

	public PojoCursor<T> find(Object id, int skip, int limit)
	{
		return find(Filters.eq("_id", id), skip, limit);
	}

	public PojoCursor<T> find(Bson filter, Bson sort)
	{
		return find(filter, sort, 0, 0);
	}

	public PojoCursor<T> find(int skip, int limit)
	{
		return find(null, skip, limit);
	}

	public PojoCursor<T> find(int limit)
	{
		return find(0, limit);
	}

	public PojoCursor<T> find(Bson filter)
	{
		return find(filter, null);
	}

	public PojoCursor<T> find(Object id)
	{
		return find(Filters.eq("_id", id));
	}

	public PojoCursor<T> find()
	{
		return find(null);
	}

	public PojoCursor<T> findPaged(Bson filter, Bson projection, Bson sort, int page, int perPage)
	{
		return find(filter, projection, sort, (page - 1) * perPage, perPage);
	}

	public PojoCursor<T> findPaged(Object id, Bson projection, Bson sort, int page, int perPage)
	{
		return findPaged(Filters.eq("_id", id), projection, sort, page, perPage);
	}

	public PojoCursor<T> findPaged(Bson filter, Bson sort, int page, int perPage)
	{
		return findPaged(filter, null, sort, page, perPage);
	}

	public PojoCursor<T> findPaged(Object id, Bson sort, int page, int perPage)
	{
		return findPaged(Filters.eq("_id", id), sort, page, perPage);
	}

	public PojoCursor<T> findPaged(Bson sort, int page, int perPage)
	{
		return findPaged(null, sort, page, perPage);
	}

	public long count()
	{
		return count(null);
	}

	public long estimatedCount()
	{
		return getCollection().estimatedDocumentCount();
	}

	public long count(Bson filter)
	{
		if (filter == null)
			return getCollection().countDocuments();
		return getCollection().countDocuments(filter);
	}

	protected AggregateIterable<T> getAggregateIterable(List<Bson> pipeline)
	{
		return getCollection().aggregate(pipeline);
	}

	public PojoCursor<T> aggregate(List<Bson> pipeline)
	{
		return new PojoCursor<T>(getAggregateIterable(pipeline).cursor());
	}

	public PojoCursor<T> aggregate(Bson... stages)
	{
		List<Bson> pipeline = new ArrayList<Bson>();
		if (stages != null && stages.length > 0)
			for (Bson stage : stages)
				pipeline.add(stage);
		return aggregate(pipeline);
	}

	public void drop()
	{
		getCollection().drop();
	}
}
