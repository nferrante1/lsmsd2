package app.common.net;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.common.net.entities.Entity;
import app.common.net.enums.ActionRequest;

public class ResponseMessage extends Message
{
	private static final long serialVersionUID = -4469582259015203553L;

	protected final boolean success;
	protected final String errorMsg;

	public ResponseMessage(String errorMsg)
	{
		this(false, errorMsg, new ArrayList<Entity>());
	}

	public ResponseMessage(List<Entity> entities)
	{
		this(true, null, entities);
	}

	public ResponseMessage(Entity... entities)
	{
		this(true, null, entities);
	}

	protected ResponseMessage(boolean success, String errorMsg, List<Entity> entities)
	{
		super(entities);
		this.success = success;
		this.errorMsg = errorMsg;
	}

	protected ResponseMessage(boolean success, String errorMsg, Entity... entities)
	{
		this(success, errorMsg, Arrays.asList(entities));
	}

	public boolean isSuccess()
	{
		return success;
	}

	public boolean isValid(ActionRequest actionRequest)
	{
		if (!isSuccess())
			return getEntityCount() == 0;
		switch (actionRequest) {
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

	public static ResponseMessage receive(DataInputStream input)
	{
		return (ResponseMessage)Message.receive(input);
	}

	public String getErrorMsg()
	{
		return errorMsg;
	}
}