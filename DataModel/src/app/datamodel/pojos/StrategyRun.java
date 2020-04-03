package app.datamodel.pojos;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;


public class StrategyRun extends Pojo {
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
