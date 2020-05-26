package app.common.net.entities;

import app.common.net.entities.enums.ParameterType;

public class ParameterInfo extends Entity
{
	private static final long serialVersionUID = 1605222247978013829L;

	private final String name;
	private final ParameterType type;

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

	public ParameterType getType()
	{
		return type;
	}
}
