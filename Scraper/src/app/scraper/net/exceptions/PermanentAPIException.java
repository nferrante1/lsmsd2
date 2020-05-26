package app.scraper.net.exceptions;

public class PermanentAPIException extends RuntimeException
{
	private static final long serialVersionUID = -1411105917144056372L;

	public PermanentAPIException()
	{
		super();
	}

	public PermanentAPIException(String message)
	{
		super(message);
	}
}
