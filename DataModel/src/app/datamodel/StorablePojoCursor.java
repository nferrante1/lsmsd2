package app.datamodel;

import com.mongodb.client.MongoCursor;

import app.datamodel.pojos.StorablePojo;

public class StorablePojoCursor<T extends StorablePojo> extends PojoCursor<T>
{
	StorablePojoCursor(MongoCursor<T> cursor)
	{
		super(cursor);
	}

	StorablePojoCursor(PojoCursor<T> cursor)
	{
		super(cursor);
	}

	@Override
	public T next()
	{
		T pojo = super.next();
		if (pojo != null)
			pojo.initialized();
		return pojo;
	}
}
