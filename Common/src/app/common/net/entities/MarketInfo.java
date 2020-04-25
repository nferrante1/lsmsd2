package app.common.net.entities;

public class MarketInfo extends Entity
{
	private static final long serialVersionUID = 4212262098735608977L;

	String id;
	int granularity;
	boolean sync;
	boolean selectable;

	public MarketInfo()
	{
	}

	public MarketInfo(String id, int granularity, boolean sync, boolean enabled)
	{
		this.id = id;
		this.granularity = granularity;
		this.sync = sync;
		this.selectable = enabled;
	}

	public MarketInfo(String id, int granularity)
	{
		this.id = id;
		this.granularity = granularity;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public int getGranularity()
	{
		return granularity;
	}

	public void setGranularity(int granularity)
	{
		this.granularity = granularity;
	}

	public boolean isSync()
	{
		return sync;
	}

	public void setSync(boolean sync)
	{
		this.sync = sync;
	}

	public boolean isSelectable()
	{
		return selectable;
	}

	public void setSelectable(boolean enabled)
	{
		this.selectable = enabled;
	}
}
