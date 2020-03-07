package app.datamodel.mongo;

import org.bson.conversions.Bson;

public abstract class EmbeddedPojo extends Pojo
{
	private transient Pojo container;
	private transient boolean embeddedList;

	@SuppressWarnings("unchecked")
	protected <T extends Pojo> T getContainer()
	{
		return (T)container;
	}
	
	protected void setContainer(Pojo container)
	{
		this.container = container;
	}
	
	protected void setEmbeddedList(boolean value)
	{
		this.embeddedList = value;
	}
	
	protected void setEmbeddedList()
	{
		setEmbeddedList(true);
	}
	
	public boolean isEmbeddedList()
	{
		return embeddedList;
	}

	public static String getFieldName(Class<? extends EmbeddedPojo> objType)
	{
		if (!objType.isAnnotationPresent(Embedded.class))
			return objType.getName();
		String nestedName = objType.getAnnotation(Embedded.class).nestedName();
		if (nestedName.isEmpty())
			return objType.getName();
		return nestedName;
	}
	
	public static Class<? extends Pojo> getContainerClass(Class<? extends EmbeddedPojo> objType)
	{
		return objType.isAnnotationPresent(Embedded.class)
			? objType.getAnnotation(Embedded.class).value() : Pojo.class;
	}
	
	public Class<? extends Pojo> getContainerClass()
	{
		if (container != null)
			return container.getClass();
		return getContainerClass(this.getClass());
	}
	
	public Bson getContainerFilter()
	{
		return container.getFilter();
	}
	
	public String getFieldName()
	{
		return getFieldName(this.getClass());
	}
}
