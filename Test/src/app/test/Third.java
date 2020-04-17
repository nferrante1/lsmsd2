package app.test;

import org.bson.codecs.pojo.annotations.BsonProperty;

import app.datamodel.pojos.CollectionName;
import app.datamodel.pojos.PojoId;
import app.datamodel.pojos.StorablePojo;
import app.datamodel.pojos.StorablePojoState;

@CollectionName("TestCollection")
public class Third extends StorablePojo
{
	@PojoId
	@BsonProperty("idField")
	private String name;
	@BsonProperty("thirdCustomName")
	private String inStr;
	private int inInt;
	
	public Third()
	{
		super();
	}
	
	public Third(String name, String inStr, int inInt)
	{
		super(StorablePojoState.UNTRACKED);
		this.name = name;
		this.inStr = inStr;
		this.inInt = inInt;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		updateField("name", name);
	}

	public String getInStr()
	{
		return inStr;
	}

	public void setInStr(String inStr)
	{
		updateField("inStr", inStr);
	}

	public int getInInt()
	{
		return inInt;
	}

	public void setInInt(int inInt)
	{
		updateField("inInt", inInt);
	}

}
