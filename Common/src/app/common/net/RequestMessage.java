package app.common.net;

import java.io.DataInputStream;

import app.common.net.entities.Entity;
import app.common.net.enums.ActionRequest;

public class RequestMessage extends Message
{
	private static final long serialVersionUID = 6989601732466426604L;

	protected final ActionRequest action;
	protected final String authToken;

	public RequestMessage(ActionRequest action, String authToken, Entity... entities)
	{
		super(entities);
		this.authToken = authToken;
		this.action = action;
	}

	public RequestMessage(ActionRequest action, Entity... entities)
	{
		this(action, null, entities);
	}

	public ActionRequest getAction()
	{
		return action;
	}
	
	public String getAuthToken()
	{
		return authToken;
	}

	public static RequestMessage receive(DataInputStream input)
	{
		return (RequestMessage)Message.receive(input);
	}

	public boolean isValid()
	{
		switch(action) {
		default:
			return true;
		}
	}
}
