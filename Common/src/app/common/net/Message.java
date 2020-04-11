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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import app.datamodel.pojos.AuthToken;
import app.common.net.Message;

/**
 * Represent a message exchanged between server and client.
 */
public class Message implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2948263922164031956L;
	protected ActionRequest messageType;
	
	public Message() 
	{
		
	}
	
	public Message(ActionRequest type) 
	{
		this.messageType = type;
	}

	protected String toXML()
	{
		XStream xs = new XStream();
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xs.toXML(this);
	}

	protected static Message fromXML(String xml)
	{
		XStream xs = new XStream();
		xs.addPermission(NoTypePermission.NONE);
		xs.addPermission(NullPermission.NULL);
		xs.addPermission(PrimitiveTypePermission.PRIMITIVES);
		xs.allowTypeHierarchy(Collection.class);
		xs.allowTypesByWildcard(new String[] {
			"app.common.net.entities.**",
			"app.common.net.**"
		});
		return (Message)xs.fromXML(xml);
	}

	/**
	 * Sends the XML-serialized message through the specified output stream.
	 * @param output The output stream.
	 */
	public void send(DataOutputStream output)
	{
		String xml = this.toXML();
		Logger.getLogger(Message.class.getName()).fine(Thread.currentThread().getName() + ": SENDING\n" + xml);
		try {
			output.writeUTF(xml);
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
		String xml;
		try {
			xml = input.readUTF();
			Logger.getLogger(Message.class.getName()).fine(Thread.currentThread().getName() + ": RECEIVED\n" + xml);
			return fromXML(xml);
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
