package app.common.net.entities;

public final class ProgressInfo extends Entity
{
	private static final long serialVersionUID = 5331206537641352490L;

	private final double progress;

	public ProgressInfo(double progress)
	{
		this.progress = progress;
	}

	public double getProgress()
	{
		return progress;
	}

}
