package app.datamodel.pojos;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import app.datamodel.pojos.enums.StorablePojoState;

public abstract class StorablePojo
{
	protected transient HashMap<String, Object> updatedFields = new HashMap<String, Object>();
	protected transient StorablePojoState state;
	protected transient boolean deleted;

	public StorablePojo()
	{
		this(StorablePojoState.INIT);
	}

	public StorablePojo(StorablePojoState state)
	{
		this.state = state;
	}

	@BsonIgnore
	public HashMap<String,Object> getUpdatedFields()
	{
		if (isDeleted())
			throw new IllegalStateException("Trying to get updates for a deleted Pojo.");
		if (!isTracked())
			throw new IllegalStateException("Trying to get updates of an untracked Pojo.");
		if (!isStaged())
			return null;
		return updatedFields;
	}

	protected void updateField(String name, Object value)
	{
		if (isDeleted())
			throw new IllegalStateException("Trying to edit a deleted Pojo.");

		Field field;
		try {
			field = this.getClass().getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalArgumentException();
		}
		Object oldValue;
		try {
			field.setAccessible(true);
			oldValue = field.get(this);
			if((oldValue != null && oldValue.equals(value))
				|| (oldValue == null && value == null))
				return;
			field.set(this, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		if (Modifier.isTransient(field.getModifiers()))
			return;

		registerUpdate(field.getName(), oldValue, value);
	}

	private void registerUpdate(String name, Object oldValue, Object newValue)
	{
		if (isDeleted() || isDeleting())
			throw new IllegalStateException("Trying to register an update in a deleted Pojo.");
		if (!isTracked())
			return;
		if (!updatedFields.containsKey(name)) {
			updatedFields.put(name, oldValue);
		} else {
			Object origValue = updatedFields.get(name);
			if((origValue != null && origValue.equals(newValue))
				|| (origValue == null && newValue == null)) {
				updatedFields.remove(name);
				if (updatedFields.isEmpty())
					setState(StorablePojoState.COMMITTED);
				return;
			}
		}
		setState(StorablePojoState.STAGED);
	}

	@BsonIgnore
	public StorablePojoState getState()
	{
		return state;
	}

	@BsonIgnore
	protected void setState(StorablePojoState state)
	{
		this.state = state;
	}

	private void commitSubPojos()
	{
		for (Field field: this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (Modifier.isTransient(field.getModifiers()))
				continue;
			if (StorablePojo.class.isAssignableFrom(field.getType())) {
				try {
					StorablePojo pojo = (StorablePojo)field.get(this);
					if (pojo == null)
						continue;
					if (!pojo.isDeleted())
						pojo.commit();
					if (pojo.isDeleted())
						field.set(this, null);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				continue;
			}
			if (!List.class.isAssignableFrom(field.getType()))
				continue;
			ParameterizedType type = (ParameterizedType) field.getGenericType();
			Class<?> genericType = (Class<?>)type.getActualTypeArguments()[0];
			if (!StorablePojo.class.isAssignableFrom(genericType))
				continue;
			try {
				@SuppressWarnings("unchecked")
				ListIterator<? extends StorablePojo> iterator = ((List<? extends StorablePojo>)field.get(this)).listIterator();
				while (iterator.hasNext()) {
					StorablePojo pojo = iterator.next();
					if (!pojo.isDeleted())
						pojo.commit();
					if (pojo.isDeleted())
						iterator.remove();
			}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void commit()
	{
		if (isDeleted())
			throw new IllegalStateException("Call to commit() on a deleted Pojo.");
		if (isInitializing())
			throw new IllegalStateException("Call to commit() on a Pojo in " + state + " state.");
		if (isIgnored())
			return;
		if (!isDeleting())
			commitSubPojos();
		if (isCommitted())
			return;
		if (isStaged())
			updatedFields.clear();
		setState(StorablePojoState.COMMITTED);
	}

	public void delete()
	{
		if (isDeleting() || isIgnored())
			return;
		if (isDeleted())
			throw new IllegalStateException("Call to deleted() on an already deleted Pojo.");
		if (!isTracked())
			throw new IllegalStateException("Call to delete() on an untracked Pojo.");
		setState(StorablePojoState.STAGED);
		deleted = true;
	}

	public void detach()
	{
		if (isDeleted())
			throw new IllegalStateException("Call to detach() on a deleted Pojo.");
		updatedFields.clear();
		setState(StorablePojoState.IGNORED);
	}

	public void initialized()
	{
		if (!isInitializing())
			return;
		for (Field field: this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (Modifier.isTransient(field.getModifiers()))
				continue;
			if (StorablePojo.class.isAssignableFrom(field.getType())) {
				try {
					StorablePojo pojo = (StorablePojo)field.get(this);
					pojo.initialized();
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				continue;
			}
			if (!List.class.isAssignableFrom(field.getType()))
				continue;
			ParameterizedType type = (ParameterizedType) field.getGenericType();
			Class<?> genericType = (Class<?>)type.getActualTypeArguments()[0];
			if (!StorablePojo.class.isAssignableFrom(genericType))
				continue;
			try {
				@SuppressWarnings("unchecked")
				List<? extends StorablePojo> list = (List<? extends StorablePojo>)field.get(this);
				for (StorablePojo pojo: list)
					pojo.initialized();
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		setState(StorablePojoState.COMMITTED);
	}

	@BsonIgnore
	public boolean isDeleted()
	{
		return deleted && isCommitted();
	}

	@BsonIgnore
	public boolean isDeleting()
	{
		return deleted && isStaged();
	}

	@BsonIgnore
	public boolean isTracked()
	{
		return !isInitializing() && !isIgnored() && !isUntracked() && !isDeleted();
	}

	@BsonIgnore
	public boolean isUntracked()
	{
		return state == StorablePojoState.UNTRACKED || state == null;
	}

	@BsonIgnore
	public boolean isIgnored()
	{
		return state == StorablePojoState.IGNORED;
	}

	@BsonIgnore
	public boolean isInitializing()
	{
		return state == StorablePojoState.INIT;
	}

	@BsonIgnore
	public boolean isStaged()
	{
		return state == StorablePojoState.STAGED;
	}

	@BsonIgnore
	public boolean isCommitted()
	{
		return state == StorablePojoState.COMMITTED;
	}
}
