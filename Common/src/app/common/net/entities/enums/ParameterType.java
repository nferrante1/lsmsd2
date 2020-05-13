package app.common.net.entities.enums;

import java.io.Serializable;
import java.time.Instant;

public enum ParameterType implements Serializable
{
	INTEGER,
	DOUBLE,
	BOOLEAN,
	INSTANT,
	STRING;

	public static ParameterType getFromType(Class<?> type)
	{
		if (Integer.class.equals(type) || Integer.TYPE.equals(type))
			return INTEGER;
		if (Double.class.equals(type) || Double.TYPE.equals(type))
			return DOUBLE;
		if (Boolean.class.equals(type) || Boolean.TYPE.equals(type))
			return BOOLEAN;
		if (Instant.class.equals(type))
			return INSTANT;
		if (String.class.equals(type))
			return STRING;
		return null;
	}
}
