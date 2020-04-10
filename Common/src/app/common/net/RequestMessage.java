package app.common.net;

import app.datamodel.pojos.AuthToken;
/**
 * Represents a request message, sent from the client to the server.
 */
public class RequestMessage extends Message
{
	
	protected String authToken;

	/**
	 * Creates a new request message, with optional entities attached.
	 * @param action The type of action requested.
	 * @param entities The list of entities to attach.
	 */
	public RequestMessage(ActionRequest action, String authToken)
	{
		super(action);
		this.authToken = authToken;
	}

	public String getAuthToken()
	{
		return authToken;
	}

	public void setAuthToken(String authToken)
	{
		this.authToken = authToken;
	}

	
}
