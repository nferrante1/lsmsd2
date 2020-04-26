package app.common.net.entities;

import app.common.net.entities.enums.ParameterType;

public class KVParameter extends ParameterInfo
{
	private static final long serialVersionUID = 5154503119353580029L;

	protected String value;

	public KVParameter(String name, String value, ParameterType type)
	{
		super(name, type);
		this.value = value;
	}
	
	public KVParameter(String name, String value)
	{
		super(name);
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}
