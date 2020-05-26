package app.common.net.entities;

public final class SourceInfo extends Entity
{
	private static final long serialVersionUID = 2089675492960252661L;

	private final String name;
	private boolean enabled;

	public SourceInfo(String name, boolean enabled)
	{
		this.name = name;
		this.enabled = enabled;
	}

	public String getName()
	{
		return name;
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