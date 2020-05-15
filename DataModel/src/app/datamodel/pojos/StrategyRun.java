package app.datamodel.pojos;

import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.annotations.PojoId;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("Strategies")
public class StrategyRun extends StorablePojo
{
	@PojoId
	protected ObjectId id;
	protected String user;
	protected Map<String, Object> parameters;
	protected Report report;

	public StrategyRun()
	{
		super();
	}

	public StrategyRun(String user, Map<String, Object> parameters, Report report)
	{
		super(StorablePojoState.UNTRACKED);
		this.id = new ObjectId();
		this.user = user;
		this.parameters = parameters;
		this.report = report;
	}

	public StrategyRun(Map<String, Object> parameters, Report report)
	{
		this(null, parameters, report);
	}

	public ObjectId getId()
	{
		return id;
	}

	public void setId(ObjectId id)
	{
		updateField("id", id);
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		updateField("user", user);
	}

	public Map<String, Object> getParameters()
	{
		return this.parameters;
	}

	public void setParameters(Map<String, Object> parameters)
	{
		this.parameters = parameters;
	}

	public Report getReport()
	{
		return report;
	}

	public void setReport(Report report)
	{
		updateField("report", report);
	}

	@BsonIgnore
	public Object getParameter(String name)
	{
		return parameters.get(name);
	}
}
