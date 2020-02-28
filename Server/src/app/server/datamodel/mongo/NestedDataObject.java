package app.server.datamodel.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public abstract class NestedDataObject extends DataObject
{
	private transient DataObject container;

	protected DataObject getContainer()
	{
		return container;
	}
	
	public void setContainer(DataObject container)
	{
		this.container = container;
	}
	
	@Override
	protected HashMap<String, Object> composeIdFilter()
	{
		HashMap<String,Object> hm = super.composeIdFilter(getClass().getAnnotation(CollectionName.class).nestedName());
		hm.putAll(container.composeIdFilter());
		return hm;
	}
	
	@Override
	public void delete() 
	{
		getDB().updateOne(getCollectionName(), container.getIdFilter(), Updates.pull(getClass().getAnnotation(CollectionName.class).nestedName(), super.getIdFilter()));
	}
}
