package app.datamodel.mongo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import app.datamodel.DataSource;

public abstract class Pojo
{
	private transient HashMap<String, Object> updatedFields = new HashMap<String, Object>();
	private transient boolean saved;
	
	public Pojo()
	{
	}
	
	protected void setSaved(boolean value)
	{
		saved = value;
	}
	
	protected void setSaved()
	{
		setSaved(true);
	}
	
	public boolean isSaved()
	{
		return saved;
	}
	
	protected Bson getFilter()
	{
		for (Field field: this.getClass().getDeclaredFields())
			if (field.isAnnotationPresent(BsonId.class)) {
				field.setAccessible(true);
				try {
					return Filters.eq(field.getName(), field.get(this));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
			}
		throw new UnsupportedOperationException("Class " + this.getClass().getCanonicalName() + " does not specify a BsonId.");
	}
	
	protected Document getUpdateDocument()
	{
		Document document = new Document();
		for (Map.Entry<String, Object> field: updatedFields.entrySet())
			document.append(field.getKey(), field.getValue());
		updatedFields.clear();
		return document;

	}
	
	protected void updateField(String name, Object value)
	{
		updatedFields.put(name, value);
	}

	public static <T extends Pojo> String getCollectionName(Class<T> objType)
	{
		return objType.isAnnotationPresent(CollectionName.class)
			? objType.getAnnotation(CollectionName.class).value() : objType.getName();
	}
	
	public String getCollectionName()
	{
		return getCollectionName(this.getClass());
	}
}
