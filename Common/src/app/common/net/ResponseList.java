package app.common.net;

import java.util.ArrayList;
import java.util.List;

public class ResponseList<T> extends ResponseMessage {
	List<T> list = new ArrayList<T>();

	public ResponseList()
	{
		super();
	}
	public List<T> getList()
	{
		return list;
	}

	public void setList(List<T> list)
	{
		this.list = list;
	}
	
	public void add(T value) 
	{
		this.list.add(value);
	}
	
}
