package app.common.net;

/**
 * Represents a response message, sent from the server to the client after each
 * request.
 */
public class ResponseMessage extends Message
{

	protected final boolean success;
	protected final String errorMsg;

	public ResponseMessage() {
		super();
		this.success = true;
		this.errorMsg = "";
	}
	
	public ResponseMessage(ActionRequest type, boolean success)
	{
		this(type, success, "");
	}
	
	public ResponseMessage(ActionRequest type, boolean success, String errorMsg)
	{
		super(type);
		this.success = success;
		this.errorMsg = errorMsg;
	}
	
	
	public boolean isSuccess()
	{
		return success;
	}

	/**
	 * Returns the error message associated with the response.
	 * @return The error message.
	 */
	public String getErrorMsg()
	{
		return errorMsg;
	}
}
