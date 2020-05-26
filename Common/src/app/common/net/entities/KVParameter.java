package app.common.net.entities;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import app.common.net.entities.enums.ParameterType;

public final class KVParameter extends ParameterInfo
{
	private static final long serialVersionUID = 5154503119353580029L;

	private final String value;

	public KVParameter(String name, String value, ParameterType type)
	{
		super(name, type);
		this.value = value;
	}

	public KVParameter(String name, Instant value)
	{
		this(name, value.toString(), ParameterType.INSTANT);
	}

	public KVParameter(String name, String value)
	{
		this(name, value, ParameterType.STRING);
	}

	public KVParameter(String name, int value)
	{
		this(name, Integer.toString(value), ParameterType.INTEGER);
	}

	public KVParameter(String name, double value)
	{
		this(name, Double.toString(value), ParameterType.DOUBLE);
	}

	public KVParameter(String name, boolean value)
	{
		this(name, Boolean.toString(value), ParameterType.BOOLEAN);
	}

	public String getValue()
	{
		return value;
	}

	public Object getConvertedValue()
	{
		if (value == null)
			return null;
		switch(getType()) {
		case BOOLEAN:
			return Boolean.parseBoolean(value);
		case STRING:
			return value;
		case DOUBLE:
			return Double.parseDouble(value);
		case INSTANT:
			return Instant.parse(value);
		case INTEGER:
			return Integer.parseInt(value);
		default:
			return null;
		}
	}

	public boolean isValid()
	{
		if (value == null)
			return false;
		switch(getType()) {
		case BOOLEAN:
		case STRING:
			return true;
		case DOUBLE:
			try {
				Double.parseDouble(value);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		case INSTANT:
			try {
				Instant.parse(value);
				return true;
			} catch (DateTimeParseException e) {
				return false;
			}
		case INTEGER:
			try {
				Integer.parseInt(value);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		default:
			return false;
		}
	}
}
