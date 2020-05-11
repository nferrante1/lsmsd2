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

	public String getDisplayName()
	{
		char[] chars = name.toCharArray();
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(chars[0]));
		for (int i = 1; i < chars.length; i++) {
			char c = chars[i];
			if (c == '_') {
				sb.append(' ');
				continue;
			}
			if (Character.isUpperCase(c))
				sb.append(' ');
			sb.append(c);
		}
		return sb.toString();
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
