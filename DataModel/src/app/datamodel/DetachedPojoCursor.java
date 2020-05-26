package app.datamodel;

import com.mongodb.client.MongoCursor;

import app.datamodel.pojos.StorablePojo;

public class DetachedPojoCursor<T extends StorablePojo> extends StorablePojoCursor<T>
{
	DetachedPojoCursor(MongoCursor<T> cursor)
	{
		super(cursor);
	}

	DetachedPojoCursor(PojoCursor<T> cursor)
	{
		super(cursor);
	}

	public T next()
	{
		T pojo = super.next();
		if (pojo != null)
			pojo.detach();
		return pojo;
	}
}
