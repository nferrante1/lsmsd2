package app.datamodel.mongo;

import java.util.List;

import com.mongodb.client.MongoCollection;
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
}
