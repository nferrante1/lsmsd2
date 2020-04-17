package app.common.net;

import app.common.net.entities.Entity;

/**
 * Represents a request message, sent from the client to the server.
 */
public class RequestMessage extends Message
{
	private static final long serialVersionUID = 6989601732466426604L;

	protected final ActionRequest action;

	/**
	* Creates a new request message, with optional entities attached.
	* @param action The type of action requested.
	* @param entities The list of entities to attach.
	*/
	public RequestMessage(ActionRequest action, Entity... entities)
	{
		super(entities);
		this.action = action;
	}

	/**
	* Returns the type of action requested.
	* @return The type of action.
	*/
	public ActionRequest getAction()
	{
		return action;
	}

	/**
	* Checks whether this message is valid (properly formed).
	* @return True if valid; False otherwise.
	*/
	public boolean isValid()
	{
		switch(action) {
		default:
			return true;
		}
	}
}
