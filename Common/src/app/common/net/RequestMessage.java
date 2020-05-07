package app.common.net;

import java.io.DataInputStream;
import java.util.Arrays;
import java.util.List;

import app.common.net.entities.Entity;
import app.common.net.enums.ActionRequest;

public class RequestMessage extends Message
{
	private static final long serialVersionUID = 6989601732466426604L;

	protected final ActionRequest action;
	protected final String authToken;

	public RequestMessage(ActionRequest action, String authToken, List<Entity> entities)
	{
		super(entities);
		this.authToken = authToken;
		this.action = action;
	}

	public RequestMessage(ActionRequest action, String authToken, Entity... entities)
	{
		this(action, authToken, Arrays.asList(entities));
	}

	public RequestMessage(ActionRequest action, List<Entity> entities)
	{
		this(action, null, entities);
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
		switch (action) {
		// TODO: check validity
		default:
			return true;
		}
	}
}
