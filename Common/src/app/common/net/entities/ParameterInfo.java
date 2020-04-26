package app.common.net.entities;

import app.common.net.entities.enums.ParameterType;

public class ParameterInfo extends Entity
{
	private static final long serialVersionUID = 1605222247978013829L;

	protected String name;
	protected ParameterType type;

	public ParameterInfo()
	{
	}

	public ParameterInfo(String name, ParameterType type)
	{
		this.name = name;
		this.type = type;
	}

	public ParameterInfo(String name)
	{
		this(name, ParameterType.STRING);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getCapitalizedName()
	{
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public ParameterType getType()
	{
		return type;
	}

	public void setType(ParameterType type)
	{
		this.type = type;
	}
}
