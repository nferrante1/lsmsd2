package app.common.net;

import java.io.DataInputStream;
import java.util.Arrays;
import java.util.List;

import app.common.net.entities.Entity;
import app.common.net.entities.KVParameter;
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

	private boolean hasDuplicateParameters()
	{
		List<KVParameter> parameters = getEntities(KVParameter.class);
		for (KVParameter parameter1: parameters)
			for (KVParameter parameter2: parameters)
				if (parameter2.getName().equals(parameter1.getName()))
					return true;
		return false;
	}

	private boolean hasValidParameters()
	{
		List<KVParameter> parameters = getEntities(KVParameter.class);
		for (KVParameter parameter: parameters)
			if (!parameter.isValid() || parameter.getName() == null)
				return false;
		return !hasDuplicateParameters();
	}

	private boolean mayHaveParameters(String... names)
	{
		List<KVParameter> parameters = getEntities(KVParameter.class);
		outerloop: for (KVParameter parameter: parameters) {
			for (String name: names)
				if (parameter.getName().equals(name))
					continue outerloop;
			return false;
		}
		return true;
	}

	private boolean mustHaveParameters(String... names)
	{
		List<KVParameter> parameters = getEntities(KVParameter.class);
		outerloop: for (String name: names) {
			for (KVParameter parameter: parameters)
				if (parameter.getName().equals(name)) {
					if (parameter.getValue() == null)
						return false;
					continue outerloop;
				}
			return false;
		}
		return true;
	}

	public boolean isValid()
	{
		switch (action) {
		case ADD_STRATEGY:
			return true;
		case ADD_USER:
			return true;
		case BROWSE_DATA_SOURCES:
			return true;
		case BROWSE_MARKETS:
			return true;
		case BROWSE_REPORTS:
			return true;
		case BROWSE_STRATEGIES:
			return true;
		case BROWSE_USERS:
			return true;
		case DELETE_DATA:
			return true;
		case DELETE_REPORT:
			return true;
		case DELETE_STRATEGY:
			return true;
		case DELETE_USER:
			return true;
		case DOWNLOAD_STRATEGY:
			return true;
		case EDIT_DATA_SOURCE:
			return true;
		case EDIT_MARKET:
			return true;
		case GET_STRATEGY_PARAMETERS:
			return true;
		case LOGIN:
			return true;
		case LOGOUT:
			return true;
		case RUN_STRATEGY:
			return true;
		case VIEW_REPORT:
			return true;
		default:
			return false;
		}
	}
}
