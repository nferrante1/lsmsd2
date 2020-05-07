package app.datamodel.pojos;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("Strategies")
public class Strategy extends StorablePojo
{
	@BsonId
	protected String id;
	protected String name;
	protected String author;
	protected List<StrategyRun> runs = new ArrayList<StrategyRun>();

	public Strategy()
	{
		super();
	}

	public Strategy(String id, String name, String author)
	{
		super(StorablePojoState.UNTRACKED);
		this.name = name;
		this.author = author;
		this.id = id;
	}

	public void addRun(StrategyRun run)
	{
		runs.add(run);
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

	public String getId()
	{
		return this.id;
	}

	public String getUsername()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		updateField("author", author);
	}

	public List<StrategyRun> getRuns()
	{
		return runs;
	}

	public void setRuns(List<StrategyRun> runs)
	{
		this.runs = runs;
	}

	public void setId(String id)
	{
		updateField("id", id);
	}
}
