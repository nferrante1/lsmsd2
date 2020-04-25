package app.common.net.entities;

public class StrategyInfo extends Entity
{
	private static final long serialVersionUID = 8337583857656650614L;

	protected String name;
	protected String username;
	protected boolean canDelete;

	public StrategyInfo()
	{
	}

	public StrategyInfo(String name)
	{
		this(name, null, false);
	}

	public StrategyInfo(String name, String author, boolean canDelete)
	{
		this.name = name;
		this.username = author;
		this.canDelete = canDelete;
	}

	public String getName()
	{
		return name;
	}

	public String getUsername()
	{
		return username;
	}

	public boolean isCanDelete()
	{
		return canDelete;
	}

	public void setCanDelete(boolean del)
	{
		this.canDelete = del;
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
