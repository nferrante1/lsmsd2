package app.datamodel.mongo;

import java.util.HashMap;

import org.bson.Document;
import org.bson.conversions.Bson;

public abstract class Pojo
{
	private transient HashMap<String, Object> updatedFields;

	protected Bson getFilter()
	{
		return null;
	}
	
	protected Document getUpdateDocument()
	{
		return null;
	}
	
	protected void updateField(String name, Object value)
	{

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
