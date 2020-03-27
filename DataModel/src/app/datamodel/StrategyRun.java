package app.datamodel;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Embedded;
import app.datamodel.mongo.EmbeddedPojo;
import app.datamodel.mongo.EmbeddedPojoManager;


@CollectionName("Strategies")
@Embedded(value = Strategy.class, nestedName = "runs", list=true)
public class StrategyRun extends EmbeddedPojo {
	@BsonId
	protected ObjectId id;
	protected String user;
	protected List<Parameter<?>> config;
	protected transient Report report;
	
	
	public StrategyRun(String user, List<Parameter<?>> config, Report report)
	{
		this.id = new ObjectId();
		this.user = user;
		this.config = config;
		this.report = report;
	}
	
	public List<Parameter<?>> getConfig()
	{
		return this.config;
	}	
}
