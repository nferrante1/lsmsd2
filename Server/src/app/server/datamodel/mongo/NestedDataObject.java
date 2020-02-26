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
	Bson getIdFilter()
	{
		List<Bson> filters = new ArrayList<Bson>();
		HashMap<String, Object> hm = composeIdFilter();
		String nestedName = getClass().getAnnotation(CollectionName.class).nestedName();
		
		for(Map.Entry<String, Object> mapEntry : hm.entrySet()) 
		{
			filters.add(Filters.eq(nestedName + "." + mapEntry.getKey(), mapEntry.getValue()));
		}
		
		hm = container.composeIdFilter();
		for(Map.Entry<String, Object> mapEntry : hm.entrySet()) 
		{
			filters.add(Filters.eq(mapEntry.getKey(), mapEntry.getValue()));
		}	
		
		return filters.size() > 1 ? Filters.and(filters) : filters.get(0);
	}
	
	@Override
	public void delete() 
	{
		getDB().updateOne(getCollectionName(), container.getIdFilter(), Updates.pull(getClass().getAnnotation(CollectionName.class).nestedName(), super.getIdFilter()));
	}
}
