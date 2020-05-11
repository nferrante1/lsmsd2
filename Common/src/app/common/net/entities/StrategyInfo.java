package app.common.net.entities;

public class StrategyInfo extends Entity
{
	private static final long serialVersionUID = 8337583857656650614L;

	protected String name;
	protected String author;
	protected boolean deletable;

	public StrategyInfo()
	{
	}

	public StrategyInfo(String name, String author, boolean deletable)
	{
		this.name = name;
		this.author = author;
		this.deletable = deletable;
	}

	public String getName()
	{
		return name;
	}

	public String getAuthor()
	{
		return author;
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

	public void setAuthor(String username)
	{
		this.author = username;
	}
}
