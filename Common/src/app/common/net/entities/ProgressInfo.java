package app.common.net.entities;

public class ProgressInfo extends Entity
{
	private static final long serialVersionUID = 5331206537641352490L;

	protected final int percent;

	public ProgressInfo(int percent)
	{
		this.percent = percent;
	}

	public int getPercent()
	{
		return percent;
	}

}
