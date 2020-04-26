package app.common.net.entities;

public class StrategyInfo extends Entity
{
	private static final long serialVersionUID = 8337583857656650614L;

	protected String name;
	protected String username;
	protected boolean deletable;

	public StrategyInfo()
	{
	}

	public String getName()
	{
		return name;
	}

	public String getUsername()
	{
		return username;
	}

	public boolean isDeletable()
	{
		return deletable;
	}

	public void setDeletable(boolean deletable)
	{
		this.deletable = deletable;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}
}
