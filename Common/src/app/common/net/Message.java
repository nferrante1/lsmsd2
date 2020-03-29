package app.common.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;

import app.datamodel.pojos.AuthToken;

/**
 * Represent a message exchanged between server and client.
 */
public abstract class Message
{
	
	protected ActionRequest messageType;
	
	public Message() 
	{
		
	}
	
	public Message(ActionRequest type) 
	{
		this.messageType = type;
	}

	public String toJson()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public static Message fromJson(String json, Type T)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, T);
	}
	

	public static Message fromJson(String json)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, Message.class);
	}
	
	
	public void send(DataOutputStream output)
	{
		Gson gson = new Gson();
		String json = this.toJson();
		Logger.getLogger(Message.class.getName()).fine(Thread.currentThread().getName() + ": SENDING\n" + json);
		try {
			output.writeUTF(json);
		} catch (IOException ex) {
			Logger.getLogger(Message.class.getName()).warning("Failure in sending message.");
		}
	}

	/**
	 * Receives an XML-serialized message from the specified input stream.
	 * @param input The input stream.
	 * @return An instance of Message representing the deserialized message.
	 */
	public static Message receive(DataInputStream input)
	{
		String json;
		try {
			json = input.readUTF();
			Logger.getLogger(Message.class.getName()).fine(Thread.currentThread().getName() + ": RECEIVED\n" + json);
			return fromJson(json);
		} catch (IOException ex) {
			Logger.getLogger(Message.class.getName()).warning("Failure in receiving message. Probably counterpart terminated.");
			return null;
		}
	}

	public ActionRequest getMessageType()
	{
		return messageType;
	}

	public void setMessageType(ActionRequest messageType)
	{
		this.messageType = messageType;
	}
}
