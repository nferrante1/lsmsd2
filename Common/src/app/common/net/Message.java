package app.common.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import app.common.net.entities.Entity;

class Message implements Serializable
{
	private static final long serialVersionUID = -5181705765357502182L;

	protected final List<Entity> entities = new ArrayList<Entity>();

	protected Message(List<Entity> entities)
	{
		if (entities == null)
			return;
		for(Entity entity : entities)
			this.entities.add(entity);
	}

	protected Message(Entity... entities)
	{
		this(Arrays.asList(entities));
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
		//xs.addImplicitArray(Message.class, "entities", Entity.class);
		xs.allowTypesByWildcard(new String[] {
			"app.common.net.entities.**",
			"app.common.net.**",
			"app.common.net.enums.**",
			"app.common.net.entities.enums.**"
		});
		return (Message)xs.fromXML(xml);
	}

	public void send(DataOutputStream output)
	{
		String xml = this.toXML();
		try {
			System.out.println(xml);
			output.writeUTF(xml);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

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

	public List<Entity> getEntities()
	{
		return entities;
	}

	public Entity getEntity(int index)
	{
		if (index < 0 || index >= getEntityCount())
			return null;
		return entities.get(index);
	}

	public Entity getEntity()
	{
		return getEntity(0);
	}

	public void addEntity(Entity entity)
	{
		entities.add(entity);
	}

	public int getEntityCount()
	{
		return entities.size();
	}
}
