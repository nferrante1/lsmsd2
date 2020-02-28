package app.server.datamodel.mongo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

	public static <T extends NestedDataObject> List<T> loadEmbedded(Class<T> objType, Bson filter, String sortField, boolean ascending, int pageNumber, int perPage)
	{
		Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantTypeConverter()).create();
		List<Document> documents = DBManager.getInstance().findEmbedded(getCollectionName(objType), filter, objType.getAnnotation(CollectionName.class).nestedName(), sortField, ascending, pageNumber*perPage, perPage);
		List<T> sources = new ArrayList<T>();
		for (Document document: documents) {
			T source = gson.fromJson(document.toJson(), objType);
			source.postLoad();
			sources.add(source);
		}
		return sources;
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
