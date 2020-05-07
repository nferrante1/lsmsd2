package app.datamodel.pojos;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.annotations.PojoId;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("Strategies")
public class Parameter<T> extends StorablePojo
{
	@PojoId
	private String name;
	private T value;

	public Parameter(String name, T value)
	{
		super(StorablePojoState.UNTRACKED);
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		updateField("name", name);
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T value)
	{
		updateField("value", value);
	}
}
