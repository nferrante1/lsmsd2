package app.common.net;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.common.net.entities.AuthTokenInfo;
import app.common.net.entities.BaseReportInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.FileContent;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.ParameterInfo;
import app.common.net.entities.ProgressInfo;
import app.common.net.entities.ReportInfo;
import app.common.net.entities.SourceInfo;
import app.common.net.entities.StrategyInfo;
import app.common.net.entities.UserInfo;
import app.common.net.enums.ActionRequest;

public final class ResponseMessage extends Message
{
	private static final long serialVersionUID = -4469582259015203553L;

	private final boolean success;
	private final String errorMsg;

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
			return getEntityCount() == 1 && hasEntity(StrategyInfo.class);

		case BROWSE_DATA_SOURCES:
			for (Entity entity: getEntities())
				if (!SourceInfo.class.isAssignableFrom(entity.getClass()))
					return false;
			return true;
		case BROWSE_MARKETS:
			for (Entity entity: getEntities())
				if (!MarketInfo.class.isAssignableFrom(entity.getClass()))
					return false;
			return true;

		case BROWSE_REPORTS:
			for (Entity entity: getEntities())
				if (!BaseReportInfo.class.isAssignableFrom(entity.getClass()))
					return false;
			return true;

		case BROWSE_STRATEGIES:
			for (Entity entity: getEntities())
				if (!StrategyInfo.class.isAssignableFrom(entity.getClass()))
					return false;
			return true;

		case BROWSE_USERS:
			for (Entity entity: getEntities())
				if (!UserInfo.class.isAssignableFrom(entity.getClass()))
					return false;
			return true;

		case DOWNLOAD_STRATEGY:
			return getEntityCount() == 1 && hasEntity(FileContent.class);

		case GET_STRATEGY_PARAMETERS:
			for (Entity entity: getEntities())
				if (!ParameterInfo.class.isAssignableFrom(entity.getClass()))
					return false;
			return true;
		case LOGIN:
			return getEntityCount() == 1 && hasEntity(AuthTokenInfo.class);
		case ADD_USER:
		case EDIT_DATA_SOURCE:
		case EDIT_MARKET:
		case DELETE_DATA:
		case DELETE_REPORT:
		case DELETE_STRATEGY:
		case DELETE_USER:
		case LOGOUT:
			return getEntityCount() == 0;
		case RUN_STRATEGY:
			return getEntityCount() == 1 && (hasEntity(ReportInfo.class) || hasEntity(ProgressInfo.class));
		case VIEW_REPORT:
			return getEntityCount() == 1 && hasEntity(ReportInfo.class);
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