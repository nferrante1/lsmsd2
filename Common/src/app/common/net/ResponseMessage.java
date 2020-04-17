package app.common.net;

import java.io.DataInputStream;

import app.common.net.entities.Entity;

/**
 * Represents a response message, sent from the server to the client after each
 * request.
 */
public class ResponseMessage extends Message
{
	private static final long serialVersionUID = -4469582259015203553L;

	protected final boolean success;
	protected final String errorMsg;

	/**
	* Creates a new error response message, with the specified error
	* message.
	* @param errorMsg The error message.
	*/
	public ResponseMessage(String errorMsg)
	{
		this(false, errorMsg, (Entity[])null);
	}

	/**
	* Creates a new response message, with optional attached entities.
	* @param entities The list of entities to attach.
	*/
	public ResponseMessage(Entity... entities)
	{
		this(true, null, entities);
	}

	protected ResponseMessage(boolean success, String errorMsg, Entity... entities)
	{
		super(entities);
		this.success = success;
		this.errorMsg = errorMsg;
	}

	/**
	* Checks whether this message is a response to a successfully
	* completed request or not.
	* @return True if the message is a response to a successful request;
	* False if it is a error response.
	*/
	public boolean isSuccess()
	{
		return success;
	}

	/**
	* Checks whether this message is valid (properly formed).
	* @param actionRequest The type of request that resulted in this
	* response.
	* @return True if the message is a valid response for the request type
	* specified; False otherwise.
	*/
	public boolean isValid(ActionRequest actionRequest)
	{
		if (!isSuccess())
			return getEntityCount() == 0;
		switch(actionRequest)
		{
		default:
			return true;
		}
	}

	public static ResponseMessage receive(DataInputStream input)
	{
		return (ResponseMessage)Message.receive(input);
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