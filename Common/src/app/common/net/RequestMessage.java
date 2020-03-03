package app.common.net;

import app.datamodel.AuthToken;
/**
 * Represents a request message, sent from the client to the server.
 */
public class RequestMessage extends Message
{
	
	protected AuthToken authToken;

	/**
	 * Creates a new request message, with optional entities attached.
	 * @param action The type of action requested.
	 * @param entities The list of entities to attach.
	 */
	public RequestMessage(ActionRequest action, AuthToken authToken)
	{
		super(action);
		this.authToken = authToken;
	}

	public AuthToken getAuthToken()
	{
		return authToken;
	}

	public void setAuthToken(AuthToken authToken)
	{
		this.authToken = authToken;
	}

	
}
