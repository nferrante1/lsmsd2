package app.test;

import org.bson.codecs.pojo.annotations.BsonProperty;

import app.datamodel.pojos.CollectionName;
import app.datamodel.pojos.PojoId;
import app.datamodel.pojos.StorablePojo;
import app.datamodel.pojos.StorablePojoState;

@CollectionName("TestCollection")
public class Second extends StorablePojo
{
	@PojoId
	private String name;
	@BsonProperty("innerCustomName")
	private String inStr;
	private int inInt;
	
	public Second()
	{
		super();
	}
	
	public Second(String name, String inStr, int inInt)
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
