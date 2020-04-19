package app.datamodel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;

import com.mongodb.MongoNamespace;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.CollectionName;
import app.datamodel.pojos.DataSource;

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
		getCollection(newCollectionName).drop();
		renameCollection(newCollectionName);
	}

	protected static MongoCollection<Document> getCollection(String name)
	{
		return getDB().getCollection(name);
	}
	
	protected static <X> MongoCollection<X> getCollection(String name, Class<X> clazz)
	{
		return getDB().getCollection(name, clazz);
	}

	protected MongoCollection<T> getCollection()
	{
		return getCollection(getCollectionName(), pojoClass);
	}

	/*//TODO: iterate in sub pojos
	protected static Bson getProjection(Class<? extends Object> clazz, boolean include, String... fields)
	{
		if (fields == null || fields.length == 0)
			return null;
		List<String> fieldNames = new ArrayList<String>();
		boolean includeId = !include;
		for (String fullName: fields) {
			int lastDotIndex = Math.max(fullName.lastIndexOf("."), 0);
			String prefix = fullName.substring(0, lastDotIndex);
			String fieldName = fullName.substring(lastDotIndex);
			Field field;
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				continue;
			}
			if (field.isAnnotationPresent(BsonId.class)) {
				includeId = include;
				continue;
			}
			String name = prefix;
			if (field.isAnnotationPresent(BsonProperty.class))
				name += field.getAnnotation(BsonProperty.class).value();
			else
				name += fieldName;
			fieldNames.add(name);
		}
		if (fieldNames.isEmpty())
			return null;
		Bson projection = include ? Projections.include(fieldNames) : Projections.exclude(fieldNames);
		if (includeId)
			return projection;
		return Projections.fields(projection, Projections.excludeId());
	}
	
	public static Bson getIncludeProjection(Class<? extends Object> clazz, String... fields)
	{
		return getProjection(clazz, true, fields);
	}

	public static Bson getExcludeProjection(Class<? extends Object> clazz, String... fields)
	{
		return getProjection(clazz, false, fields);
	}

	public Bson getIncludeProjection(String... fields)
	{
		return getIncludeProjection(pojoClass, fields);
	}

	public Bson getExcludeProjection(String... fields)
	{
		return getExcludeProjection(pojoClass, fields);
	}
	
	//TODO: iterate in sub pojos
	public static Bson generateFilter(Class<? extends Object> clazz, HashMap<String, Object> fields)
	{
		List<Bson> filters = new ArrayList<Bson>();
		for (Entry<String, Object> entry: fields.entrySet()) {
			String fullName = entry.getKey();
			int lastDotIndex = fullName.lastIndexOf(".");
			String prefix = fullName.substring(0, lastDotIndex + 1);
			String fieldName = fullName.substring(lastDotIndex + 1);
			Object value = entry.getValue();
			Field field;
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				continue;
			}
			if (field.isAnnotationPresent(BsonId.class))
				return Filters.eq("_id", value);
			String name = prefix;
			if (field.isAnnotationPresent(BsonProperty.class))
				name += field.getAnnotation(BsonProperty.class).value();
			else
				name += fieldName;
			filters.add(Filters.eq(name, value));
		}
		if (filters.isEmpty())
			return null;
		return filters.size() == 1 ? filters.get(0) : Filters.and(filters);
	}
	
	public static Bson generateFilter(Class<? extends Object> clazz, String field, Object value)
	{
		HashMap<String, Object> fields = new HashMap<String, Object>();
		fields.put(field, value);
		return generateFilter(clazz, fields);
	}
	
	public Bson generateFilter(HashMap<String, Object> fields)
	{
		return generateFilter(pojoClass, fields);
	}
	
	public Bson generateFilter(String field, Object value)
	{
		return generateFilter(pojoClass, field, value);
	}
	
	//TODO: iterate in sub pojos
	protected static Bson generateSort(Class<? extends Object> clazz, boolean ascending, String... fields)
	{
		if (fields == null || fields.length == 0)
			return null;
		List<String> sortFields = new ArrayList<String>();
		for (String fullName: fields) {
			int lastDotIndex = fullName.lastIndexOf(".");
			String prefix = fullName.substring(0, lastDotIndex + 1);
			String fieldName = fullName.substring(lastDotIndex + 1);
			Field field;
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				continue;
			}
			if (field.isAnnotationPresent(BsonId.class)) {
				sortFields.add("_id");
				continue;
			}
			String name = prefix;
			if (field.isAnnotationPresent(BsonProperty.class))
				name += field.getAnnotation(BsonProperty.class).value();
			else
				name += fieldName;
			sortFields.add(name);
		}
		if (sortFields.isEmpty())
			return null;
		return ascending ? Sorts.ascending(sortFields) : Sorts.descending(sortFields);
	}
	
	protected Bson generateSort(boolean ascending, String... fields)
	{
		return generateSort(pojoClass, ascending, fields);
	}
	
	public static Bson generateAscSort(Class<? extends Object> clazz, String... fields)
	{
		return generateSort(clazz, true, fields);
	}

	public static Bson generateDescSort(Class<? extends Object> clazz, String... fields)
	{
		return generateSort(clazz, false, fields);
	}
	
	public Bson generateAscSort(String... fields)
	{
		return generateSort(true, fields);
	}

	public Bson generateDescSort(String... fields)
	{
		return generateSort(false, fields);
	}*/
	
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
	
	public PojoCursor<T> find(Bson filter, Bson projection, int skip, int limit)
	{
		return find(filter, projection, null, skip, limit);
	}

	public PojoCursor<T> find(Object id, Bson projection, int skip, int limit)
	{
		return find(Filters.eq("_id", id), projection, skip, limit);
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

	public PojoCursor<T> find(Bson filter, Bson projection, Bson sort, int limit)
	{
		return find(filter, projection, sort, 0, limit);
	}
	
	public PojoCursor<T> find(Bson filter, Bson projection, int limit)
	{
		return find(filter, projection, null, limit);
	}

	public PojoCursor<T> find(Object id, Bson projection, Bson sort, int limit)
	{
		return find(Filters.eq("_id", id), projection, sort, limit);
	}

	public PojoCursor<T> find(Object id, Bson projection, int limit)
	{
		return find(Filters.eq("_id", id), projection, limit);
	}
	
	public PojoCursor<T> find(int skip, int limit, Bson sort)
	{
		return find(null, null, sort, skip, limit);
	}
	
	public PojoCursor<T> find(int limit, Bson sort)
	{
		return find(0, limit, sort);
	}

	public PojoCursor<T> find(Bson filter, Bson projection, Bson sort)
	{
		return find(filter, projection, sort, 0);
	}

	public PojoCursor<T> find(Bson filter, Bson projection)
	{
		return find(filter, projection, null);
	}

	public PojoCursor<T> find(Object id, Bson projection)
	{
		return find(Filters.eq("_id", id), projection);
	}
	
	public PojoCursor<T> find(int skip, int limit)
	{
		return find(null, skip, limit);
	}
	
	public PojoCursor<T> find(Bson filter, int limit)
	{
		return find(filter, 0, limit);
	}

	public PojoCursor<T> find(Object id, int limit)
	{
		return find(Filters.eq("_id", id), limit);
	}
	
	public PojoCursor<T> find(int limit)
	{
		return find(null, limit);
	}
	
	public PojoCursor<T> find(Bson filter)
	{
		return find(filter, 0);
	}
	
	public PojoCursor<T> findSorted(Bson sort)
	{
		return findSorted(null, sort);
	}

	public PojoCursor<T> findSorted(Bson projection, Bson sort)
	{
		return find(null, projection, sort);
	}
	
	public PojoCursor<T> find(Object id)
	{
		return find(Filters.eq("_id", id));
	}
	
	public PojoCursor<T> find()
	{
		return find(null);
	}
	
	/*protected PojoCursor<T> find(Bson filter, int skip, int limit, boolean include, String... fields)
	{
		return find(filter, getProjection(pojoClass, include, fields), skip, limit);
	}
	
	protected PojoCursor<T> findInclude(Bson filter, int skip, int limit, String... fields)
	{
		return find(filter, skip, limit, true, fields);
	}
	
	protected PojoCursor<T> findExclude(Bson filter, int skip, int limit, String... fields)
	{
		return find(filter, skip, limit, false, fields);
	}

	protected PojoCursor<T> findInclude(Bson filter, int limit, String... fields)
	{
		return findInclude(filter, 0, limit, fields);
	}
	
	protected PojoCursor<T> findExclude(Bson filter, int limit, String... fields)
	{
		return findExclude(filter, 0, limit, fields);
	}

	public PojoCursor<T> findInclude(int limit, String... fields)
	{
		return findInclude(null, limit, fields);
	}
	
	public PojoCursor<T> findExclude(int limit, String... fields)
	{
		return findExclude(null, limit, fields);
	}

	public PojoCursor<T> findInclude(String... fields)
	{
		return findInclude(0, fields);
	}
	
	public PojoCursor<T> findExclude(String... fields)
	{
		return findExclude(0, fields);
	}*/

	public PojoCursor<T> findPaged(Bson filter, Bson projection, Bson sort, int page, int perPage)
	{
		return find(filter, projection, sort, (page - 1)*perPage, perPage);
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
	
	/*protected PojoCursor<T> findPaged(Bson filter, int page, int perPage, boolean include, String... fields)
	{
		return findPaged(filter, getProjection(pojoClass, include, fields), page, perPage);
	}
	
	protected PojoCursor<T> findIncludePaged(Bson filter, int page, int perPage, String... fields)
	{
		return findPaged(filter, page, perPage, true, fields);
	}

	protected PojoCursor<T> findExcludePaged(Bson filter, int page, int perPage, String... fields)
	{
		return findPaged(filter, page, perPage, false, fields);
	}

	public PojoCursor<T> findIncludePaged(int page, int perPage, String... fields)
	{
		return findIncludePaged(null, page, perPage, fields);
	}

	public PojoCursor<T> findExcludePaged(int page, int perPage, String... fields)
	{
		return findExcludePaged(null, page, perPage, fields);
	}*/

	public long count()
	{
		return count(null);
	}
	
	protected long count(Bson filter)
	{
		if (filter == null)
			return getCollection().countDocuments();
		return getCollection().countDocuments(filter);
	}
	
	protected AggregateIterable<T> getAggregateIterable(List<Bson> pipeline)
	{
		return getCollection().aggregate(pipeline);
	}
	
	protected PojoCursor<T> aggregate(List<Bson> pipeline)
	{
		return new PojoCursor<T>(getAggregateIterable(pipeline).cursor());
	}
	
	public void drop()
	{
		getCollection().drop();
	}
}
