package app.common.net.entities;

import java.util.HashMap;

public class BrowseInfo extends Entity
{
	private static final long serialVersionUID = -5686831756818703207L;
	protected final HashMap<String, String> filter;
	protected final int page;
	//protected final String perPage;
	
	public BrowseInfo(HashMap<String, String> filter, int page)
	{
		this.filter= filter;
		this.page = page;
	}
	

	public BrowseInfo(String filter, int page)
	{
		this(new HashMap<String, String>(), page);
		this.filter.put("default", filter);
	}
	
	public BrowseInfo(String filter)
	{
		this(filter, -1);
	}
	
	public BrowseInfo(int page)
	{
		this.filter = null;
		this.page = page;
	}
	
	public String getFilter()
	{
		return this.filter.get("default");
	}
	
	public String getFilter(String key)
	{
		return this.filter.get(key);
	}
	
	
	public int getPage()
	{
		return page;
	}
	
}
