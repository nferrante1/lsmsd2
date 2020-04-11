package app.common.net.entities;

public class DataSource {
	String id;
	boolean enabled;
	public DataSource(String id, boolean enabled) 
	{
		this.id = id;
		this.enabled = enabled;
	}
	
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public boolean isEnabled()
	{
		return enabled;
	}
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}
