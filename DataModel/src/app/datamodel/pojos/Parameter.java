package app.datamodel.pojos;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonIgnore;

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

	@BsonIgnore
	public static Parameter<?> getParameter(String name, Object value)
	{
		Class<?> type = value.getClass();
		if (Integer.class.equals(type))
			return new Parameter<Integer>(name, (Integer)value);
		if (Integer.TYPE.equals(type))
			return new Parameter<Integer>(name, (int)value);
		if (Double.class.equals(type))
			return new Parameter<Double>(name, (Double)value);
		if (Double.TYPE.equals(type))
			return new Parameter<Double>(name, (double)value);
		if (Boolean.class.equals(type))
			return new Parameter<Boolean>(name, (Boolean)value);
		if (Boolean.TYPE.equals(type))
			return new Parameter<Boolean>(name, (boolean)value);
		if (Instant.class.equals(type))
			return new Parameter<Instant>(name, (Instant)value);
		if (String.class.equals(type))
			return new Parameter<String>(name, (String)value);
		return new Parameter<Object>(name, value);
	}
}
