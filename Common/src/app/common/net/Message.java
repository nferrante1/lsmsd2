package app.common.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import app.common.net.entities.Entity;

public class Message implements Serializable
{
	private static final long serialVersionUID = -5181705765357502182L;

	protected final List<Entity> entities = new ArrayList<Entity>();

	protected Message(Entity... entities)
	{
		if (entities != null)
			for (Entity entity: entities)
				this.entities.add(entity);
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
		try {
			output.writeUTF(xml);
		} catch (IOException ex) {
			ex.printStackTrace();
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
			return fromXML(xml);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	* Returns the list of entities attached to the message.
	* @return The list of entities attached to the message.
	*/
	public List<Entity> getEntities()
	{
		return entities;
	}

	/**
	* Returns the attached entity at the specified index inside the list.
	* <p>
	* NOTE: it's not guaranteed that entities' order is maintained after
	* deserialization.
	* </p>
	* @param index The index of the entity.
	* @return The entity.
	*/
	public Entity getEntity(int index)
	{
		if (index < 0 || index >= getEntityCount())
			return null;
		return entities.get(index);
	}

	/**
	* Returns the first attached entity.
	* @return The entity.
	*/
	public Entity getEntity()
	{
		return getEntity(0);
	}

	/**
	* Attach an entity to the message.
	* @param entity The entity to attach.
	*/
	public void addEntity(Entity entity)
	{
		entities.add(entity);
	}

	/**
	* Returns the number of entities attached to this message.
	* @return The number of entities attached.
	*/
	public int getEntityCount()
	{
		return entities.size();
	}
}
