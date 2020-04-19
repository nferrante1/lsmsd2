package app.common.net.entities;

public class BrowseInfo extends Entity
{
	private static final long serialVersionUID = -5686831756818703207L;
	protected final String filter;
	protected final int page;
	//protected final String perPage;

	public BrowseInfo(String filter, int page)
	{
		this.filter = filter;
		this.page = page;
	}
	
	public BrowseInfo(String filter)
	{
		this(filter, -1);
	}
	
	public BrowseInfo(int page)
	{
		this(null, page);
	}
	
	public String getFilter()
	{
		return filter;
	}
	
	public int getPage()
	{
		return page;
	}
	
	public boolean isFilter() {
		if(this.filter.equals("*"))
			return false;
		return true;
	}

}
