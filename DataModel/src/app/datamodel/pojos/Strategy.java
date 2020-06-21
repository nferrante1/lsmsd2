package app.datamodel.pojos;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("Strategies")
public final class Strategy extends StorablePojo
{
	@BsonId
	private String id;
	private String name;
	private String author;
	private List<StrategyRun> runs = new ArrayList<StrategyRun>();

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

	@BsonIgnore
	public StrategyRun getRun(String id)
	{
		for(StrategyRun r: this.runs)
			if(r.getId().toHexString().equals(id))
				return r;
		return null;
	}
	public String getName()
	{
		return this.name;
	}

	public String getId()
	{
		return this.id;
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

	public String getAuthor()
	{
		return author;
	}
}
