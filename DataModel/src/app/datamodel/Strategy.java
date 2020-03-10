package app.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.conversions.Bson;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

@CollectionName("Strategies")
public class Strategy extends Pojo {
	@SerializedName("_id")
	@BsonId
	protected String id;
	protected String name;
	protected String username;
	protected List<StrategyRun> runs = new ArrayList<StrategyRun>();
	private static transient PojoManager<Strategy> manager;
	
	public Strategy(String name, String username, byte[] file)
	{
		super();
		this.name = name;
		this.username = username;
		this.id = "STRATEGIONA"; 
	}
	
	public void addRun(StrategyRun run)
	{
		runs.add(run);
	}
	

	private Strategy()
	{
		super();
	}
	
	public static PojoManager<Strategy> getManager()
	{
		if(manager == null)
			manager = new PojoManager<Strategy>(Strategy.class);
		return manager;
	}
	public static List<Strategy> load(int pageNumber, int perPage)
	{
		return manager.find(null, "name", true, pageNumber, perPage);
	}	
	
	public static List<Strategy> load(String name, int pageNumber, int perPage)
	{
		Bson filter = null;
		if(name != null)
			filter = Filters.regex("name", name);
		return manager.find(filter, "name", true, pageNumber, perPage);
	}


	public void setName(String name)
	{
		updateField("name", name);
	}
	
	public StrategyRun getRun(int index)
	{
		return runs.get(index);
	}

	public String getName()
	{
		return this.name;
	}
}
