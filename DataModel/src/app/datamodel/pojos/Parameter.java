package app.datamodel.pojos;


@CollectionName("Strategies")
public class Parameter<T> extends StorablePojo
{
	private String name;
	private T value;
	
	public Parameter(String name, T value)
	{
		super(StorablePojoState.UNTRACKED);
		this.name = name;
		this.value = value;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	
	public T getValue()
	{
		return value;
	}
	
	
	public void setValue(T value)
	{
		this.value = value;
	}
	
	
}
