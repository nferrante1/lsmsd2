package app.datamodel.pojos;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;

public class Pojo {
	protected transient HashMap<String, Object> updatedFields = new HashMap<String, Object>();
	protected transient PojoState state;
	
	public Pojo() 
	{
		this(PojoState.FETCHING);
	}
	
	public Pojo(PojoState state)
	{
		this.state = state;
	}
	
	public HashMap<String,Object> getUpdatedFields()
	{
		return updatedFields;
	}
	
	protected void updateField(String name, Object value)
	{
		Field field;
		if (value instanceof List<?>)
			throw new IllegalArgumentException();
		try {
			field = this.getClass().getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalArgumentException();
		}
		if (Modifier.isTransient(field.getModifiers()))
			return;
		try {
			field.setAccessible(true);
			Object fieldValue = field.get(this);
			if(fieldValue != null && fieldValue.equals(value))
				return;
			field.set(this, value);
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
		
		registerUpdate(field.getName(), value);
	}

	private void registerUpdate(String name, Object value)
	{
		if(state != PojoState.COMMITTED) return;
		
		updatedFields.put(name, value);		
	}

	public PojoState getState()
	{
		return state;
	}

	public void setState(PojoState state)
	{
		if(state == PojoState.COMMITTED)
			updatedFields.clear();
		this.state = state;
	}
}
