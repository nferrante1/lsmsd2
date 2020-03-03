package app.datamodel;

import org.bson.types.ObjectId;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.DataObjectId;
import app.datamodel.mongo.NestedDataObject;

@CollectionName("Strategies")
public class StrategyRun extends NestedDataObject {
	@DataObjectId
	protected ObjectId id;
	protected String user;
	protected Config config;
	protected transient Report report;
	
	public StrategyRun(String user, Config config, Report report)
	{
		this.id = new ObjectId();
		this.user = user;
		this.config = config;
		this.report = report;
	}
	
	public Config getConfig()
	{
		return config;
	}
	
	
}
