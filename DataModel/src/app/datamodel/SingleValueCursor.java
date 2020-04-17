package app.datamodel;

import java.util.ArrayList;
import java.util.List;

import app.datamodel.pojos.SingleValue;

public class SingleValueCursor<T extends Object> extends PojoCursor<SingleValue<T>>
{
	public SingleValueCursor(PojoCursor<SingleValue<T>> cursor)
	{
		super(cursor);
	}

	public T nextValue()
	{
		SingleValue<T> singleValue = super.next();
		return singleValue == null ? null : singleValue.getValue();
	}
	
	public List<T> toValueList()
	{
		List<T> values = new ArrayList<T>();
		for (T value = nextValue(); value != null; value = nextValue())
			values.add(value);
		return values;
	}
}
