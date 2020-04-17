package app.datamodel.pojos;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.conversions.Bson;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;


@CollectionName("Strategies")
public class Strategy extends StorablePojo {
	@BsonId
	protected String id;
	protected String name;
	protected String username;
	protected List<StrategyRun> runs = new ArrayList<StrategyRun>();
	
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
	
	public void setName(String name)
	{
		updateField("name", name);
	}
	
	@BsonIgnore
	public StrategyRun getRun(int index)
	{
		return runs.get(index);
	}

	public String getName()
	{
		return this.name;
	}
}
