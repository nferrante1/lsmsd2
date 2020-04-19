package app.common.net.entities;

public class SourceInfo extends Entity
{
	private String name;
	private boolean enabled;
	
	public SourceInfo() {}

	public String getName()
	{
		return name;
	}

	public void set_id(String name)
	{
		this.name = name;
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