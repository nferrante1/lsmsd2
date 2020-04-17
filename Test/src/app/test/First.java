package app.test;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import app.datamodel.pojos.CollectionName;
import app.datamodel.pojos.StorablePojo;
import app.datamodel.pojos.StorablePojoState;

@CollectionName("TestCollection")
public class First extends StorablePojo
{
	@BsonId
	private String id;
	@BsonProperty("customName")
	private String strField;
	private int intField;
	@BsonProperty("inner")
	private Second second;
	private List<Second> seconds = new ArrayList<Second>();
	private transient boolean notRelevant;

	public First()
	{
		super();
	}

	public First(String id, String strField, int intField, Second second)
	{
		super(StorablePojoState.UNTRACKED);
		this.id = id;
		this.strField = strField;
		this.intField = intField;
		this.second = second;
	}
	
	public List<Second> getSeconds()
	{
		return seconds;
	}
	
	@BsonIgnore
	public boolean isNotRelevant()
	{
		return notRelevant;
	}

	@BsonIgnore
	public void setNotRelevant(boolean value)
	{
		notRelevant = value;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		updateField("id", id);
	}

	public String getStrField()
	{
		return strField;
	}

	public void setStrField(String strField)
	{
		updateField("strField", strField);
	}

	public int getIntField()
	{
		return intField;
	}

	public void setIntField(int intField)
	{
		updateField("intField", intField);
	}
	
	public Second getSecond()
	{
		return second;
	}
	
	public void setSecond(Second second)
	{
		updateField("second", second);
	}
}
