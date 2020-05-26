package app.scraper.net.exceptions;

public class TemporaryAPIException extends RuntimeException
{
	private static final long serialVersionUID = -2717916708119571937L;
	private long millisToWait;

	protected TemporaryAPIException(long millisToWait)
	{
		super();
		this.millisToWait = millisToWait;
	}

	public TemporaryAPIException()
	{
		this(0);
	}

	public TemporaryAPIException(String message, long millisToWait)
	{
		super(message);
		this.millisToWait = millisToWait;
	}

	public TemporaryAPIException(String message)
	{
		this(message, 0);
	}

	public TemporaryAPIException(String message, Throwable cause, long millisToWait)
	{
		super(message, cause);
		this.millisToWait = millisToWait;
	}

	public TemporaryAPIException(String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace, long millisToWait)
	{
		super(message, cause, enableSuppression, writableStackTrace);
		this.millisToWait = millisToWait;
	}

	public long getMillisToWait()
	{
		return millisToWait;
	}
}
