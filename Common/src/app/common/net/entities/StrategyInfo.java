package app.common.net.entities;

public final class StrategyInfo extends Entity
{
	private static final long serialVersionUID = 8337583857656650614L;

	private final String name;
	private final String author;
	private final boolean deletable;

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
}
