package app.common.net.entities;

public class BrowseInfo extends Entity
{
	private static final long serialVersionUID = -5686831756818703207L;

	protected final int page;
	protected final int perPage;

	public BrowseInfo(int page, int perPage)
	{
		this.page = page;
		this.perPage = perPage;
	}

	public BrowseInfo(int page)
	{
		this(page, 20);
	}

	public BrowseInfo()
	{
		this(1);
	}

	public int getPage()
	{
		return page;
	}

	public int getPerPage()
	{
		return perPage;
	}
}
