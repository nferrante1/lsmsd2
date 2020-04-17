package app.datamodel;

import java.util.List;

import org.bson.conversions.Bson;

import app.datamodel.pojos.SingleValue;

public class SingleValueManager<T extends Object> extends PojoManager<SingleValue<T>>
{
	public SingleValueManager(Class<SingleValue<T>> pojoClass, String collectionName)
	{
		super(pojoClass, collectionName);
	}

	@Override
	protected SingleValueCursor<T> aggregate(List<Bson> pipeline)
	{
		PojoCursor<SingleValue<T>> cursor = super.aggregate(pipeline);
		return new SingleValueCursor<T>(cursor);
	}

}
