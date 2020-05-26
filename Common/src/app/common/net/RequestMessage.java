package app.common.net;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.common.net.entities.BrowseInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.FileContent;
import app.common.net.entities.KVParameter;
import app.common.net.entities.LoginInfo;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.SourceInfo;
import app.common.net.enums.ActionRequest;

public final class RequestMessage extends Message
{
	private static final long serialVersionUID = 6989601732466426604L;

	private final ActionRequest action;
	private final String authToken;

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
		List<String> names = new ArrayList<String>();

		for (KVParameter parameter: parameters)
			if(names.contains(parameter.getName()))
				return true;
			else
				names.add(parameter.getName());
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

	private boolean mustOnlyHaveParameters(String... names)
	{
		return mayHaveParameters(names) && mustHaveParameters(names);
	}

	public boolean isValid()
	{
		switch (action) {
		case BROWSE_MARKETS:
		case BROWSE_STRATEGIES:
		case BROWSE_REPORTS:
		case BROWSE_USERS:
		case GET_STRATEGY_PARAMETERS:
		case RUN_STRATEGY:
		case ADD_STRATEGY:
		case DOWNLOAD_STRATEGY:
		case DELETE_STRATEGY:
		case VIEW_REPORT:
		case DELETE_REPORT:
		case DELETE_USER:
		case DELETE_DATA:
			if (!hasValidParameters())
				return false;
			switch (action) {
			case BROWSE_MARKETS:
			case BROWSE_STRATEGIES:
			case BROWSE_REPORTS:
			case BROWSE_USERS:
				if (!hasEntity(BrowseInfo.class))
					return false;
				for (Entity entity: getEntities())
					if (!KVParameter.class.isAssignableFrom(entity.getClass()) && !BrowseInfo.class.isAssignableFrom(entity.getClass()))
						return false;
				switch (action) {
				case BROWSE_MARKETS:
					return mayHaveParameters("SOURCE", "MARKET", "FULLID");
				case BROWSE_STRATEGIES:
					return mayHaveParameters("STRATEGYNAME");
				case BROWSE_REPORTS:
					return mayHaveParameters("STRATEGYNAME", "MARKETID");
				case BROWSE_USERS:
					return mayHaveParameters("USERNAME");
				default:
					return false;
				}
			case GET_STRATEGY_PARAMETERS:
			case DOWNLOAD_STRATEGY:
			case DELETE_STRATEGY:
				return getEntityCount() == 1 && mustOnlyHaveParameters("STRATEGYNAME");
			case RUN_STRATEGY:
				for (Entity entity: getEntities())
					if (!KVParameter.class.isAssignableFrom(entity.getClass()))
						return false;
				return mustHaveParameters("STRATEGYNAME", "market", "inverseCross", "granularity");
			case ADD_STRATEGY:
				for (Entity entity: getEntities())
					if (!KVParameter.class.isAssignableFrom(entity.getClass())
						&& !FileContent.class.isAssignableFrom(entity.getClass()))
						return false;
				return mustOnlyHaveParameters("CLASSNAME")
					&& hasEntity(FileContent.class)
					&& getEntity(FileContent.class).getContent().length > 0;
			case VIEW_REPORT:
			case DELETE_REPORT:
				return getEntityCount() == 1 && mustOnlyHaveParameters("REPORTID");
			case DELETE_USER:
				return getEntityCount() == 1 && mustOnlyHaveParameters("USERNAME");
			case DELETE_DATA:
				for (Entity entity: getEntities())
					if (!KVParameter.class.isAssignableFrom(entity.getClass()))
						return false;
				return mayHaveParameters("SOURCE", "MARKET", "DATE") && mustHaveParameters("SOURCE");
			default:
				return false;
			}
		case LOGIN:
		case ADD_USER:
			return getEntityCount() == 1 && hasEntity(LoginInfo.class)
				&& getEntity(LoginInfo.class).getUsername() != null
				&& getEntity(LoginInfo.class).getUsername().matches("^[A-Za-z0-9]{3,32}$")
				&& getEntity(LoginInfo.class).getPassword() != null
				&& getEntity(LoginInfo.class).getPassword().length() > 7;
		case LOGOUT:
		case BROWSE_DATA_SOURCES:
			return getEntityCount() == 0;
		case EDIT_DATA_SOURCE:
			return getEntityCount() == 1 && hasEntity(SourceInfo.class)
				&& getEntity(SourceInfo.class).getName() != null
				&& !getEntity(SourceInfo.class).getName().isBlank();
		case EDIT_MARKET:
			return getEntityCount() == 1 && hasEntity(MarketInfo.class)
				&& getEntity(MarketInfo.class).getSourceName() != null
				&& !getEntity(MarketInfo.class).getSourceName().isBlank()
				&& getEntity(MarketInfo.class).getMarketId() != null
				&& !getEntity(MarketInfo.class).getMarketId().isBlank()
				&& getEntity(MarketInfo.class).getGranularity() > 0;
		default:
			return false;
		}
	}
}
