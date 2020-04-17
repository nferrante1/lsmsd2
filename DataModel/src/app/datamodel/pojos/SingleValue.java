package app.datamodel.pojos;

public final class SingleValue<T extends Object>
{
	private T value;

	public SingleValue()
	{
		super();
	}
	
	public SingleValue(T value)
	{
		this.value = value;
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
