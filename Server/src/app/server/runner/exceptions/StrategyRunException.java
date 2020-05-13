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

	public StrategyRunException(Throwable cause)
	{
		super(cause);
	}

	public StrategyRunException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public StrategyRunException(String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
