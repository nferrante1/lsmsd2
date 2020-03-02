package app.datamodel;

import java.util.List;

import org.bson.conversions.Bson;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.DataObject;
import app.datamodel.mongo.DataObjectId;

@CollectionName("Strategies")
public class Strategy extends DataObject {
	@SerializedName("_id")
	@DataObjectId
	protected String id;
	protected String name;
	protected String username;
	protected StrategyRun runs;
	
	public Strategy(String name, String username, byte[] file)
	{
		super();
		this.name = name;
		this.username = username;
		this.id = "STRATEGIONA"; 
	}
	

	private Strategy()
	{
		super();
	}
	public static List<Strategy> load(int pageNumber, int perPage)
	{
		return load(null, pageNumber, perPage);
	}	
	
	public static List<Strategy> load(String name, int pageNumber, int perPage)
	{
		Bson filter = null;
		if(name != null)
			filter = Filters.eq("name",name);
		return load(Strategy.class, filter, "name", true, pageNumber, perPage);
	}


	public void setName(String name)
	{
		updateField("name", name);
	}
}
