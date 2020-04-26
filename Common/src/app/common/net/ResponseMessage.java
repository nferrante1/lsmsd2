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

	public String getErrorMsg()
	{
		return errorMsg;
	}
}