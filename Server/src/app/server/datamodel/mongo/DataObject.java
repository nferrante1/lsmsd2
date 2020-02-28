package app.server.datamodel.mongo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;

public abstract class DataObject
{
	  protected static class InstantTypeConverter
	      implements JsonDeserializer<Instant> {
	    @Override
	    public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
	    throws JsonParseException {
	      return Instant.ofEpochMilli(json.getAsJsonObject().get("$date").getAsLong());
	    }
	  }
	  
	private transient Document updateDocument;
	private transient boolean saved;
	private transient DBManager db;
	
	protected DataObject(boolean saved)
	{
		db = DBManager.getInstance();
		this.saved = saved;
	}
	
	protected DataObject()
	{
		this(false);
	}
	
	public static <T extends DataObject> String getCollectionName(Class<T> objType)
	{
		return objType.isAnnotationPresent(CollectionName.class)
			? objType.getAnnotation(CollectionName.class).value() : objType.getName();
	}
	
	public String getCollectionName()
	{
		return getCollectionName(this.getClass());
	}
	
	public static <T extends DataObject> List<T> load(Class<T> objType)
	{
		return load(objType, null);
	}

	public static <T extends DataObject> List<T> load(Class<T> objType, Bson filter)
	{
		return load(objType, filter, null, false, 0, 0);
	}
	
	public static <T extends DataObject> List<T> load(Class<T> objType, Bson filter, String fieldName, boolean ascending, int pageNumber, int perPage)
	{
		Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantTypeConverter()).create();
		List<Document> documents = DBManager.getInstance().find(getCollectionName(objType), filter, fieldName, ascending, pageNumber*perPage, perPage );
		List<T> sources = new ArrayList<T>();
		for (Document document: documents) {
			T source = gson.fromJson(document.toJson(), objType);
			source.postLoad();
			sources.add(source);
		}
		return sources;
	}
	
	protected void postLoad()
	{
		for(Field field : getClass().getDeclaredFields())
		{	
			if (!Modifier.isTransient(field.getModifiers())) 
			{
				Object value;
				field.setAccessible(true);
				try 
				{
					value = field.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
					throw new UnsupportedOperationException();	
				}
				if (!(value instanceof List<?>)) 
					continue;
				List<?> list = (List<?>) value;
				
				for (Object obj: list) {
					NestedDataObject dataObj = (NestedDataObject)obj;
					dataObj.setContainer(this);
				}
			}
			
		}
		saved = true;
	}
	
	protected Document getUpdateDocument()
	{
		return (saved == false) ? null : updateDocument;
	}
	
	public boolean isSaved()
	{
		return saved;
	}
	
	protected DBManager getDB()
	{
		return db;
	}
	
	public void save()
	{
		if (saved) {
			update();
			return;
		}
		Document document = getCreateDocument();
		db.insert(getCollectionName(), document);
		saved = true;
	}
	
	Document getCreateDocument()
	{
		Document document = new Document();
		for (Field field: this.getClass().getDeclaredFields())
			if (!Modifier.isTransient(field.getModifiers())) {
				String serializedName;
				if (field.isAnnotationPresent(SerializedName.class))
					serializedName = field.getAnnotation(SerializedName.class).value();
				else
					serializedName = field.getName();
				Object value;
				try {
					field.setAccessible(true);
					value = field.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					throw new UnsupportedOperationException();
				}
				if (!(value instanceof List<?>)) {
					document.append(serializedName, value);
					continue;
				}
				List<?> list = (List<?>) value;
				List<Document> documents = new ArrayList<Document>();
				for (Object obj: list) {
					DataObject dataObj = (DataObject)obj;
					documents.add(dataObj.getCreateDocument());
				}
				document.append(serializedName, documents);
			}
		return document;
	}
	
	public void update()
	{
		if (getUpdateDocument() == null)
			return;
		db.updateOne(getCollectionName(), getIdFilter(), new Document("$set", updateDocument));
		updateDocument = null;
	}
	
	Bson getIdFilter()
	{
		return buildFilter(composeIdFilter());
	}
	
	protected HashMap<String, Object> composeIdFilter()
	{
		return composeIdFilter("");
	}
	
	protected HashMap<String, Object> composeIdFilter(String prefix)
	{
		Object value;
		
		HashMap<String, Object> filters = new HashMap<String, Object>();
		
		for (Field field: this.getClass().getDeclaredFields())
			if (!Modifier.isTransient(field.getModifiers()) && field.isAnnotationPresent(DataObjectId.class)) {
				try {
					field.setAccessible(true);
					value = field.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					continue;
				}
				String serializedName;
				if(field.isAnnotationPresent(SerializedName.class)) {
					serializedName = field.getAnnotation(SerializedName.class).value();
				} else {
					serializedName = field.getName();
				}
				filters.put(prefix+serializedName, value);
			}
		return filters;
	}
	
	protected Bson buildFilter(HashMap<String, Object>... hms) 
	{
		List<Bson> filters = new ArrayList<Bson>();
		
		for(HashMap<String, Object> hm : hms) 
		{
			
			for(Map.Entry<String, Object> mapEntry : hm.entrySet()) 
			{
				filters.add(Filters.eq(mapEntry.getKey(), mapEntry.getValue()));
			}
			
		}
		return filters.size() > 1 ? Filters.and(filters) : filters.get(0);
	}
	
	protected void updateField(String fieldName, Object value)
	{
		Field field;
		if (value instanceof List<?>)
			throw new IllegalArgumentException();
		try {
			field = this.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalArgumentException();
		}
		if (Modifier.isTransient(field.getModifiers()))
			return;
		try {
			field.setAccessible(true);
			if (field.get(this).equals(value))
				return;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
		if (updateDocument == null)
			updateDocument = new Document();
		String serializedName;
		if (field.isAnnotationPresent(SerializedName.class))
			serializedName = field.getAnnotation(SerializedName.class).value();
		else
			serializedName = fieldName;
		try {
			field.set(this, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		updateDocument.append(serializedName, value);
	}
	
	public void delete()
	{
		db.deleteOne(getCollectionName(), getIdFilter());
	}
	
	public void replace()
	{
		if (!saved) {
			save();
			return;
		}
		db.replaceOne(getCollectionName(), getIdFilter(), getCreateDocument());
	}
	
	protected List<Document> aggregate(String collectionName, List<Bson> stages)
	{
		return db.aggregate(collectionName, stages);
		
	}
}
