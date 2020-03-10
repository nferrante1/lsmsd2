package app.datamodel;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.EmbeddedPojo;
import app.datamodel.mongo.EmbeddedPojoManager;


@CollectionName("Strategies")
public class StrategyRun extends EmbeddedPojo {
	@BsonId
	protected ObjectId id;
	protected String user;
	protected Config config;
	protected transient Report report;
	private static transient EmbeddedPojoManager<StrategyRun> manager;
	
	public StrategyRun(String user, Config config, Report report)
	{
		this.id = new ObjectId();
		this.user = user;
		this.config = config;
		this.report = report;
	}
	
	public static EmbeddedPojoManager<StrategyRun> getManager()
	{
		if(manager == null)
			manager = new EmbeddedPojoManager<StrategyRun>(StrategyRun.class);
		return manager;
	}
	
	public Config getConfig()
	{
		return config;
	}
	
	
}
