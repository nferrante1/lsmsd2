package app.datamodel;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoCursor;

import app.datamodel.pojos.Pojo;
import app.datamodel.pojos.PojoState;

public class PojoCursor<T extends Pojo> implements AutoCloseable {
	private MongoCursor<T> cursor;
	
	public PojoCursor(MongoCursor<T> cursor)
	{
		//super();
		this.cursor = cursor;
	}
	
	public T next()
	{
		if(!cursor.hasNext()) return null;
		T pojo = cursor.next();
		pojo.setState(PojoState.COMMITTED);
		return pojo;
	}
	
	public void close()
	{
		cursor.close();
	}
	
	public List<T> toList() 
	{
		List<T> list = new ArrayList<T>();
		while(true) 
		{
			T pojo = next();
			if(pojo == null) break;
			list.add(pojo);
		}
		return list;
	}
}
