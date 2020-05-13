package app.common.net.entities;

public class ProgressInfo extends Entity
{
	private static final long serialVersionUID = 5331206537641352490L;

	protected final double progress;

	public ProgressInfo(double progress)
	{
		this.progress = progress;
	}

	public double getProgress()
	{
		return progress;
	}

}
