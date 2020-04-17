package app.datamodel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.PojoId;
import app.datamodel.pojos.StorablePojo;

public class StorablePojoManager<T extends StorablePojo> extends PojoManager<T>
{
	protected class UpdateInfo
	{
		protected List<Bson> filters;
		protected Bson update;
		
		public UpdateInfo(Bson update, List<Bson> filters)
		{
			this.update = update;
			this.filters = filters;
		}
		
		public UpdateInfo(Bson update)
		{
			this(update, new ArrayList<Bson>());
		}
		
		public UpdateInfo()
		{
			this(null);
		}
		
		public boolean hasUpdate()
		{
			return update != null;
		}
		
		public Bson getUpdate()
		{
			return update;
		}
		
		public List<Bson> getFilters()
		{
			return filters;
		}
		
		public boolean hasFilters()
		{
			return filters.size() > 0;
		}
		
		public void addFilter(Bson filter)
		{
			filters.add(filter);
		}
	}

	protected abstract class AbstractUpdateNode
	{
		protected List<AbstractUpdateNode> children = new ArrayList<AbstractUpdateNode>();
		protected String prefix = "";
		
		public AbstractUpdateNode(String prefix)
		{
			if (prefix != null)
				this.prefix = prefix;
		}
		
		public AbstractUpdateNode()
		{
			this(null);
		}
		
		public void setPrefix(String prefix)
		{
			if (this.prefix.equals(prefix))
				return;
			this.prefix = prefix;
			for (AbstractUpdateNode child: children)
				child.addToPrefix(getPrefix());
		}
		
		public void addToPrefix(String prePrefix)
		{
			setPrefix(prePrefix + getFieldName());
		}
		
		private String getPrefix(boolean withDot)
		{
			if (withDot)
				return (prefix == null || prefix.isEmpty()) ? "" : prefix + ".";
			return prefix;
		}
		
		public String getPrefix()
		{
			return getPrefix(true);
		}
		
		public String getFieldName()
		{
			return getPrefix(false);
		}
		
		public void addChild(AbstractUpdateNode child)
		{
			children.add(child);
			//child.addToPrefix(getPrefix());
			
		}
		
		abstract public UpdateInfo getUpdate();
	}
	
	protected class UpdateNode extends AbstractUpdateNode
	{
		protected Bson filter;
		protected HashMap<String, Object> changes = new HashMap<String, Object>();
		
		public UpdateNode(String prefix, Bson filter)
		{
			super(prefix);
			this.filter = filter;
		}
		
		public UpdateNode(Bson filter)
		{
			this(null, filter);
		}
		
		public UpdateNode(String prefix)
		{
			this(prefix, null);
		}
		
		public UpdateNode()
		{
			super();
		}
		
		public void addChange(String field, Object value)
		{
			changes.put(field, value);
		}
		
		public void setFilter(Bson filter)
		{
			this.filter = filter;
		}
		
		public Bson getFilter()
		{
			return filter;
		}
		
		public UpdateInfo getUpdate()
		{
			List<Bson> updateList = new ArrayList<Bson>();
			List<Bson> filterList = new ArrayList<Bson>();
			for (Entry<String, Object> entry: changes.entrySet()) {
				String name = getPrefix() + entry.getKey();
				Object value = entry.getValue();
				if (value == null)
					updateList.add(Updates.unset(name));
				else
					updateList.add(Updates.set(name, value));
			}
			changes.clear();
			ListIterator<AbstractUpdateNode> childIterator = children.listIterator();
			while (childIterator.hasNext()) {
				AbstractUpdateNode child = childIterator.next();
				UpdateInfo childUpdate = child.getUpdate();
				if (childUpdate == null || !childUpdate.hasUpdate()) {
					childIterator.remove();
					continue;
				}
				updateList.add(childUpdate.getUpdate());
				filterList.addAll(childUpdate.getFilters());
			}
			if (updateList.isEmpty())
				return null;
			if (filter != null)
				filterList.add(filter);
			UpdateInfo ui = new UpdateInfo(Updates.combine(updateList), filterList);
			return ui;
		}
	}
	
	protected class ListUpdateNode extends AbstractUpdateNode
	{
		protected List<StorablePojo> pushList;
		protected List<StorablePojo> pullList;
		
		public ListUpdateNode(String prefix, List<StorablePojo> pushList, List<StorablePojo> pullList)
		{
			super(prefix);
			this.pushList = pushList;
			this.pullList = pullList;
		}
		
		public ListUpdateNode(List<StorablePojo> pushList, List<StorablePojo> pullList)
		{
			this(null, pushList, pullList);
		}
		
		public ListUpdateNode()
		{
			this(null, new ArrayList<StorablePojo>(), new ArrayList<StorablePojo>());
		}
		
		public void addPush(StorablePojo pojo)
		{
			pushList.add(pojo);
		}

		public void addPull(StorablePojo pojo)
		{
			pullList.add(pojo);
		}
		
		public UpdateInfo getUpdate()
		{
			if (!pullList.isEmpty()) {
				List<StorablePojo> list = new ArrayList<StorablePojo>();
				list.addAll(pullList);
				UpdateInfo ui;
				if (list.size() == 1)
					ui = new UpdateInfo(Updates.pull(getFieldName(), list.get(0)));
				else
					ui = new UpdateInfo(Updates.pullAll(getFieldName(), list));
				pullList.clear();
				return ui;
			}
			if (!pushList.isEmpty()) {
				List<StorablePojo> list = new ArrayList<StorablePojo>();
				list.addAll(pushList);
				UpdateInfo ui;
				if (list.size() == 1)
					ui = new UpdateInfo(Updates.push(getFieldName(), list.get(0)));
				else
					ui = new UpdateInfo(Updates.pushEach(getFieldName(), list));
				pushList.clear();
				return ui;
			}
			List<Bson> updateList = new ArrayList<Bson>();
			List<Bson> filterList = new ArrayList<Bson>();
			ListIterator<AbstractUpdateNode> childIterator = children.listIterator();
			while (childIterator.hasNext()) {
				AbstractUpdateNode child = childIterator.next();
				UpdateInfo childUpdate = child.getUpdate();
				if (childUpdate == null || !childUpdate.hasUpdate()) {
					childIterator.remove();
					continue;
				}
				updateList.add(childUpdate.getUpdate());
				filterList.addAll(childUpdate.getFilters());
			}
			if (updateList.isEmpty())
				return null;
			UpdateInfo ui = new UpdateInfo(Updates.combine(updateList), filterList);
			return ui;
		}
	}
	
/******************************************************************************/
	private boolean nested;

	protected boolean isNested()
	{
		return nested;
	}
	
	public StorablePojoManager(Class<T> pojoClass)
	{
		super(pojoClass);
		for (Field field: pojoClass.getDeclaredFields()) {
			field.setAccessible(true);
			if (!Modifier.isTransient(field.getModifiers()) && field.isAnnotationPresent(BsonId.class))
				return;
		}
		nested = true;
	}
	
	public StorablePojoManager(Class<T> pojoClass, String collectionName)
	{
		super(pojoClass, collectionName);
	}
	
	public void save(T pojo)
	{
		if (isNested())
			throw new UnsupportedOperationException("Called save() on a nested Pojo.");
		if (pojo.isDeleted())
			throw new IllegalStateException("Trying to save a deleted Pojo.");
		if (pojo.isInitializing() || pojo.isIgnored())
			throw new IllegalStateException("Trying to save a Pojo in an initializing/ignored state.");
		if (pojo.isUntracked())
			insert(pojo);
		else if (pojo.isDeleting())
			delete(pojo);
		else if (pojo.isStaged() || pojo.isCommitted())
			update(pojo);
	}
	
	public void save(StorablePojo parent, T pojo, String fieldName)
	{
		save(getIdFilter(parent), pojo, fieldName);
	}

	public void save(Bson parentFilter, T pojo, String fieldName)
	{
		if (!isNested()) {
			save(pojo);
			return;
		}
		if (pojo.isDeleted())
			throw new IllegalStateException("Trying to save a deleted Pojo.");
		if (pojo.isInitializing() || pojo.isIgnored()) //FIXME: nested pojos are detached if obtained with find
			throw new IllegalStateException("Trying to save a Pojo in an initializing/ignored state.");
		if (pojo.isUntracked())
			insert(pojo);
		else if (pojo.isDeleting())
			delete(pojo);
		else if (pojo.isStaged() || pojo.isCommitted())
			update(parentFilter, pojo, fieldName);
	}
	
	public void save(List<T> pojos)
	{
		if (isNested())
			throw new UnsupportedOperationException("Called save() on a nested Pojo.");
		List<T> toDelete = new ArrayList<T>();
		List<T> toUpdate = new ArrayList<T>();
		List<T> toInsert = new ArrayList<T>();
		for (T pojo: pojos) {
			if (pojo.isDeleted())
				throw new IllegalStateException("Trying to save a deleted Pojo.");
			if (pojo.isInitializing())
				throw new IllegalStateException("Trying to save an initializing Pojo.");
			if (pojo.isIgnored())
				continue;
			if (pojo.isUntracked())
				toInsert.add(pojo);
			else if (pojo.isDeleting())
				toDelete.add(pojo);
			else if (pojo.isStaged() || pojo.isCommitted())
				toUpdate.add(pojo);
		}
		if (!toInsert.isEmpty())
			insert(toInsert);
		if (!toUpdate.isEmpty())
			update(toUpdate);
		if (!toDelete.isEmpty())
			delete(toDelete);
	}
	
	public void save(@SuppressWarnings("unchecked") T... pojos)
	{
		save(varargsToList(pojos));
	}
	
	protected List<T> varargsToList(@SuppressWarnings("unchecked") T... pojos)
	{
		List<T> pojoList = new ArrayList<T>();
		if (pojos == null || pojos.length == 0)
			return pojoList;
		for (T pojo: pojos)
			pojoList.add(pojo);
		return pojoList;
	}
	
	protected void commit(T pojo)
	{
		pojo.commit();
	}
	
	protected void commit(List<T> pojos)
	{
		for (T pojo: pojos)
			commit(pojo);
	}
	
	protected void commit(@SuppressWarnings("unchecked") T... pojos)
	{
		commit(varargsToList(pojos));
	}
	
	protected void insert(T pojo)
	{
		getCollection().insertOne(pojo);
		commit(pojo);
	}

	protected void insert(List<T> pojos)
	{
		getCollection().insertMany(pojos);
		commit(pojos);
	}
	
	protected void insert(@SuppressWarnings("unchecked") T... pojos)
	{
		insert(varargsToList(pojos));
	}
	
	public static <X extends StorablePojo> Bson getIdFilter(X pojo, String prefix)
	{
		if (pojo == null)
			return null;
		if (prefix == null || prefix.isEmpty())
			return getIdFilter(pojo);
		else if (!prefix.endsWith("."))
			prefix += ".";
		HashMap<String, Object> updatedFields;
		if (pojo.isStaged())
			updatedFields = pojo.getUpdatedFields();
		else
			updatedFields = new HashMap<String, Object>();
		List<Bson> fullFilter = new ArrayList<Bson>();
		List<Bson> filters = new ArrayList<Bson>();
		for (Field field: pojo.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (Modifier.isTransient(field.getModifiers()))
				continue;
			if (field.isAnnotationPresent(BsonId.class))
				throw new IllegalArgumentException("Embedded Pojo contains a BsonId field.");
			String fieldName = field.getName();
			String name = fieldName;
			if (field.isAnnotationPresent(BsonProperty.class))
				name = field.getAnnotation(BsonProperty.class).value();
			Object value;
			if (updatedFields.containsKey(fieldName))
				value = updatedFields.get(fieldName);
			else
				try {
					value = field.get(pojo);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			Bson filter = Filters.eq(prefix + name, value);
			if (field.isAnnotationPresent(PojoId.class))
				filters.add(filter);
			else if (filters.isEmpty())
				fullFilter.add(filter);
		}
		if (filters.isEmpty()) {
			if (fullFilter.isEmpty())
				throw new UnsupportedOperationException("Can not get id filter.");
			return fullFilter.size() == 1 ? fullFilter.get(0) : Filters.and(fullFilter);
		}
		return filters.size() == 1 ? filters.get(0) : Filters.and(filters);
	}
	
	public static <X extends StorablePojo> Bson getIdFilter(X pojo)
	{
		if (pojo == null)
			return null;
		HashMap<String, Object> updatedFields;
		if (pojo.isStaged())
			updatedFields = pojo.getUpdatedFields();
		else
			updatedFields = new HashMap<String, Object>();
		for (Field field: pojo.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (Modifier.isTransient(field.getModifiers()))
				continue;
			if (!field.isAnnotationPresent(BsonId.class))
				continue;
			String fieldName = field.getName();
			Object value;
			if (updatedFields.containsKey(fieldName))
				value = updatedFields.get(fieldName);
			else
				try {
					value = field.get(pojo);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			return Filters.eq("_id", value);
		}
		throw new UnsupportedOperationException("Specified Pojo does not define a BsonId.");
	}
	
	protected void delete(T pojo)
	{
		getCollection().deleteOne(getIdFilter(pojo));
		commit(pojo);
	}
	
	protected void delete(List<T> pojos)
	{
		for (T pojo: pojos)
			delete(pojo);
	}
	
	protected void delete(@SuppressWarnings("unchecked") T... pojos)
	{
		delete(varargsToList(pojos));
	}
	
	protected UpdateNode getUpdateNode(StorablePojo pojo)
	{
		return getUpdateNode(pojo, null);
	}
	
	protected UpdateNode getUpdateNode(StorablePojo pojo, String initialPrefix)
	{
		return getUpdateNode(pojo, initialPrefix, 0);
	}
	
	protected UpdateNode getUpdateNode(StorablePojo pojo, int level)
	{
		return getUpdateNode(pojo, null, level);
	}
	
	protected UpdateNode getUpdateNode(StorablePojo pojo, String initialPrefix, int level)
	{
		UpdateNode node = new UpdateNode(initialPrefix);
		HashMap<String, Object> updatedFields;
		if (pojo.isStaged())
			updatedFields = pojo.getUpdatedFields();
		else
			updatedFields = new HashMap<String, Object>();
		int fieldNum = 0;
		for (Field field: pojo.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (Modifier.isTransient(field.getModifiers()))
				continue;
			String fieldName = field.getName();
			String name = fieldName;
			if (field.isAnnotationPresent(BsonProperty.class))
				name = field.getAnnotation(BsonProperty.class).value();
			Object value;
			try {
				value = field.get(pojo);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			Class<?> type = field.getType();
			if (StorablePojo.class.isAssignableFrom(type)) {
				StorablePojo innerPojo = (StorablePojo)value;
				if ((innerPojo == null && updatedFields.containsKey(fieldName) && updatedFields.get(fieldName) != null)
					|| (innerPojo != null && innerPojo.isDeleting())) {
					node.addChange(name, null);
				} else if (innerPojo.isDeleted() || innerPojo.isInitializing()) {
					throw new IllegalStateException();
				} else if (innerPojo.isIgnored()) {
					continue;
				} else if (innerPojo.isUntracked()) {
					node.addChange(name, innerPojo);
				}else {
					UpdateNode innerNode = getUpdateNode(innerPojo, level+1);
					innerNode.setPrefix(name);
					node.addChild(innerNode);
				}
			} else if (List.class.isAssignableFrom(type)) {
				ParameterizedType genericType = (ParameterizedType)field.getGenericType();
				Class<?> innerType = (Class<?>)genericType.getActualTypeArguments()[0];
				if (!StorablePojo.class.isAssignableFrom(innerType))
					continue;
				@SuppressWarnings("unchecked")
				List<? extends StorablePojo> innerList = (List<? extends StorablePojo>)value;
				if (innerList.isEmpty())
					continue;
				ListUpdateNode innerNode = getListUpdateNode(innerList, fieldNum, level+1);
				innerNode.setPrefix(name);
				node.addChild(innerNode);
				fieldNum++;
			} else if (updatedFields.containsKey(fieldName)) {
				node.addChange(name, value);
			}
		}
		return node;
	}
	
	protected ListUpdateNode getListUpdateNode(List<? extends StorablePojo> pojos, int fieldNum, int level)
	{
		ListUpdateNode node = new ListUpdateNode();
		int elemNum = 0;
		for (StorablePojo pojo: pojos) {
			if (pojo.isDeleting()) {
				node.addPull(pojo);
			} else if (pojo.isInitializing()) {
				throw new IllegalStateException();
			} else if (pojo.isDeleted() || pojo.isIgnored()) {
				continue;
			} else if (pojo.isUntracked()) {
				node.addPush(pojo);
			} else {
				UpdateNode innerNode = getUpdateNode(pojo, level+1);
				String filterName = "l" + level + "f" + fieldNum + "e" + elemNum;
				Bson filter = getIdFilter(pojo, filterName);
				innerNode.setFilter(filter);
				innerNode.setPrefix("$[" + filterName + "]");
				node.addChild(innerNode);
				elemNum++;
			}
		}
		return node;
	}
	
	protected void update(T pojo)
	{
		UpdateNode updateNode = getUpdateNode(pojo);
		UpdateInfo update = updateNode.getUpdate();
		Bson filter = getIdFilter(pojo);
		while (update != null && update.hasUpdate()) {
			if (update.hasFilters()) {
				UpdateOptions options = new UpdateOptions();
				options.arrayFilters(update.getFilters());
				getCollection().updateOne(filter, update.getUpdate(), options);
			} else {
				getCollection().updateOne(filter, update.getUpdate());
			}
			update = updateNode.getUpdate();
		}
		commit(pojo);
	}
	
	protected void update(StorablePojo parent, T pojo, String prefix)
	{
		update(getIdFilter(parent), pojo, prefix);
	}

	protected void update(Bson parentFilter, T pojo, String prefix)
	{
		UpdateNode updateNode = getUpdateNode(pojo, prefix + ".$");
		UpdateInfo update = updateNode.getUpdate();
		Bson filter;
		if (parentFilter == null)
			filter = getIdFilter(pojo, prefix);
		else
			filter = Filters.and(parentFilter, getIdFilter(pojo, prefix));
		while (update != null && update.hasUpdate()) {
			if (update.hasFilters()) {
				UpdateOptions options = new UpdateOptions();
				options.arrayFilters(update.getFilters());
				getCollection().updateOne(filter, update.getUpdate(), options);
			} else {
				getCollection().updateOne(filter, update.getUpdate());
			}
			update = updateNode.getUpdate();
		}
		commit(pojo);
	}
	
	protected void update(List<T> pojos)
	{
		for (T pojo: pojos)
			update(pojo);
	}
	
	protected void update(@SuppressWarnings("unchecked") T... pojos)
	{
		update(varargsToList(pojos));
	}
	
	@Override
	public StorablePojoCursor<T> find(Bson filter, Bson projection, int skip, int limit)
	{
		PojoCursor<T> cursor = super.find(filter, projection, skip, limit);
		if (isNested())
			return new DetachedPojoCursor<T>(cursor);
		return new StorablePojoCursor<T>(cursor);
	}
	
	public boolean isPresent(T pojo)
	{
		if (isNested())
			throw new IllegalStateException("Can not check presence of a nested Pojo just by having it.");
		return count(getIdFilter(pojo)) == 1;
	}
	
	public boolean isPresent(Bson parentFilter, T pojo, String prefix)
	{
		if (!isNested())
			return isPresent(pojo);
		if (parentFilter == null)
			return count(getIdFilter(pojo, prefix)) == 1;
		return count(Filters.and(parentFilter, getIdFilter(pojo, prefix))) == 1;
	}
	
	public boolean isPresent(StorablePojo parent, T pojo, String prefix)
	{
		return isPresent(getIdFilter(parent), pojo, prefix);
	}
	
	@Override
	protected DetachedPojoCursor<T> aggregate(List<Bson> pipeline)
	{
		PojoCursor<T> cursor = super.aggregate(pipeline);
		return new DetachedPojoCursor<T>(cursor);
	}
	
	public void push(T pojo, String fieldName, List<? extends StorablePojo> items)
	{
		try {
			Field field = pojoClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			if (field.isAnnotationPresent(BsonProperty.class))
				fieldName = field.getAnnotation(BsonProperty.class).value();
		} catch (NoSuchFieldException | SecurityException e) {
		}
		getCollection().updateOne(getIdFilter(pojo), Updates.pushEach(fieldName, items));
	}

	public void push(T pojo, String fieldName, StorablePojo item)
	{
		try {
			Field field = pojoClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			if (field.isAnnotationPresent(BsonProperty.class))
				fieldName = field.getAnnotation(BsonProperty.class).value();
		} catch (NoSuchFieldException | SecurityException e) {
		}
		getCollection().updateOne(getIdFilter(pojo), Updates.push(fieldName, item));
	}
	
	public void pull(T pojo, String fieldName, List<? extends StorablePojo> items)
	{
		try {
			Field field = pojoClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			if (field.isAnnotationPresent(BsonProperty.class))
				fieldName = field.getAnnotation(BsonProperty.class).value();
		} catch (NoSuchFieldException | SecurityException e) {
		}
		getCollection().updateOne(getIdFilter(pojo), Updates.pullAll(fieldName, items));
	}
	
	public void pull(T pojo, String fieldName, StorablePojo item)
	{
		try {
			Field field = pojoClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			if (field.isAnnotationPresent(BsonProperty.class))
				fieldName = field.getAnnotation(BsonProperty.class).value();
		} catch (NoSuchFieldException | SecurityException e) {
		}
		getCollection().updateOne(getIdFilter(pojo), Updates.pull(fieldName, item));
	}
}
