package app.common.net.entities;

public class Filter extends Entity
{
	private static final long serialVersionUID = 3680148499033387577L;

	protected final String filter;

	public Filter(String filter)
	{
		this.filter = filter;
	}

	public String getFilter()
	{
		return filter;
	}
}
