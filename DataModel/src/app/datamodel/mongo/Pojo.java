package app.datamodel.mongo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import app.datamodel.DataSource;

public abstract class Pojo
{
	protected transient HashMap<String, Object> updatedFields = new HashMap<String, Object>();
	private transient boolean saved;
	private transient List<Bson> arrayFilter = new ArrayList<Bson>();
	
	public Pojo()
	{
	}

	@BsonIgnore
	protected void setSaved(boolean value)
	{
		saved = value;
	}

	@BsonIgnore
	protected void setSaved()
	{
		setSaved(true);
	}
	

	@BsonIgnore
	
	public boolean isSaved()
	{
		return saved;
	}
	
	@BsonIgnore
	protected Bson getFilter()
	{
		for (Field field: this.getClass().getDeclaredFields())
			if (field.isAnnotationPresent(BsonId.class)) {
				field.setAccessible(true);
				try {
					return Filters.eq("_id", field.get(this));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
			}
		throw new UnsupportedOperationException("Class " + this.getClass().getCanonicalName() + " does not specify a BsonId.");
	}

	@BsonIgnore
	protected Bson getUpdateDocument()
	{
		if(updatedFields.size() == 0) return null;
		Bson document = new Document();
		for (Map.Entry<String, Object> field: updatedFields.entrySet()) {
			Field fieldEmbedded;
			try {
				fieldEmbedded = this.getClass().getDeclaredField(field.getKey());
			} catch (NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException();
			}
			if(fieldEmbedded.isAnnotationPresent(Embedded.class))
			{
				fieldEmbedded.setAccessible(true);
				Object fieldInstance;
				try {
					fieldInstance = fieldEmbedded.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new UnsupportedOperationException();
				}
				if(fieldInstance instanceof List<?>) 
				{
					List<? extends EmbeddedPojo> listField = (List<? extends EmbeddedPojo>)fieldInstance;
					for(EmbeddedPojo pojo : listField)
					{
						document = Updates.combine(document, pojo.getUpdateDocument());
					}
				}
				else
				{
					document = Updates.combine(document, ((EmbeddedPojo)fieldInstance).getUpdateDocument());
				}
			} 
			else
				document = Updates.combine(document, Updates.set(field.getKey(), field.getValue()));
		}
		updatedFields.clear();
		return document;

	}
	
	protected void updateField(String name, Object value)
	{
		Field field;
		if (value instanceof List<?>)
			throw new IllegalArgumentException();
		try {
			field = this.getClass().getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalArgumentException();
		}
		if (Modifier.isTransient(field.getModifiers()))
			return;
		try {
			field.setAccessible(true);
			Object fieldValue = field.get(this);
			if(fieldValue != null && fieldValue.equals(value))
				return;
			field.set(this, value);
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
		
		registerUpdate(field.getName(), value);
	}

	@BsonIgnore
	public static <T extends Pojo> String getCollectionName(Class<T> objType)
	{
		return objType.isAnnotationPresent(CollectionName.class)
			? objType.getAnnotation(CollectionName.class).value() : objType.getName();
	}

	@BsonIgnore
	public String getCollectionName()
	{
		return getCollectionName(this.getClass());
	}
	
	protected void registerUpdate(String name, Object value, String embeddedName, HashMap<String,Bson> embeddedFilter) 
	{
		if(!isSaved())
			return;
		
		if(embeddedFilter != null && embeddedFilter.size() > 0) {
			Document document = new Document();
			for(Map.Entry<String, Bson> filter : embeddedFilter.entrySet())
				document.append("f"+arrayFilter.size()+"."+filter.getKey(), filter.getValue());
			updatedFields.put(embeddedName+".$[f"+arrayFilter.size()+"]."+name, value);
			arrayFilter.add(document);			
		} else if(embeddedName != null) 
		{
			updatedFields.put(embeddedName+"."+name, value);
		} else 
		{
			updatedFields.put(name, value);
		}
		
	}
	
	protected void registerUpdate(String name, Object value, String embeddedName) 
	{
		registerUpdate(name, value, embeddedName, null);
	}
	
	protected void registerUpdate(String name, Object value) 
	{
		registerUpdate(name, value, null, null);
	}
	
	
}
