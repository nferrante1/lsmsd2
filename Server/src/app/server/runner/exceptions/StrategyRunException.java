package app.server.runner.exceptions;

public class StrategyRunException extends RuntimeException
{
	private static final long serialVersionUID = 6294668637220862295L;

	public StrategyRunException()
	{
		super();
	}

	public StrategyRunException(String message)
	{
		super(message);
	}

	public StrategyRunException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
