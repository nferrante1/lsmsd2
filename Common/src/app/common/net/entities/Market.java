package app.common.net.entities;

public class Market {
	String name;
	String granularity;
	boolean sync;
	boolean selectable;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getGranularity()
	{
		return granularity;
	}
	public void setGranularity(String granularity)
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
	public void setSelectable(boolean selectable)
	{
		this.selectable = selectable;
	}
}
