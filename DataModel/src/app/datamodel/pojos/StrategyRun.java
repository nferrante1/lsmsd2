package app.datamodel.pojos;

import java.util.List;

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
	protected List<Parameter<?>> config;
	protected transient Report report;

	public StrategyRun(String user, List<Parameter<?>> config, Report report)
	{
		super(StorablePojoState.UNTRACKED);
		this.user = user;
		this.config = config;
		this.report = report;
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

	public List<Parameter<?>> getConfig()
	{
		return this.config;
	}

	public void setConfig(List<Parameter<?>> config)
	{
		this.config = config;
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
	public Parameter<?> getParameter(String name)
	{
		for (Parameter<?> parameter: config)
			if (parameter.getName().equals(name))
				return parameter;
		return null;
	}
}
