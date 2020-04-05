package app.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.mongodb.client.model.Updates;

import app.datamodel.mongo.DBManager;

import app.datamodel.pojos.PojoState;
import app.datamodel.pojos.User;

public class UsersManager {
	
	protected static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}

	protected MongoCollection<User> getCollection()
	{
		return getDB().getCollection("Users", User.class);
	}
	
	public void save(User user)
	{
		switch(user.getState())
		{
		case STAGED:
			insert(user);
			return;
		case COMMITTED:
			update(user);
			return;
		case REMOVED:
			delete(user);
			return;
		default:
		}
	}
	
	public void insert(User user) 
	{
		getCollection().insertOne(user);
		user.setState(PojoState.COMMITTED);
	}
	
	public void insert(List<User> users)
	{
		getCollection().insertMany(users);
		for(User u : users) {
			u.setState(PojoState.COMMITTED);
		}
	}
	
	public boolean delete(User user)
	{
		user.setState(PojoState.REMOVED);
		return getCollection().deleteOne(Filters.eq("_id", user.getName())).wasAcknowledged();		
	}
	
	public long delete(List<User> users) 
	{
		long result = 0;
		for(User user : users)
			if(delete(user))
				result++;
		return result;
	}
	
	public boolean update(User user) 
	{
		List<Bson> updateDocument = new ArrayList<Bson>();

		HashMap<String, Object> updatedFields = user.getUpdatedFields();
		for(Map.Entry<String, Object> entry : updatedFields.entrySet())
			updateDocument.add(Updates.set(entry.getKey(), entry.getValue()));
		
		user.setState(PojoState.COMMITTED);
		
		if(updateDocument.isEmpty())
			return false;
		
		//STAMPA
		System.out.println(Updates.combine(updateDocument).toBsonDocument(BsonDocument.class,CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().conventions(Arrays.asList(Conventions.ANNOTATION_CONVENTION, Conventions.SET_PRIVATE_FIELDS_CONVENTION)).automatic(true).build()))).toJson());

		return getCollection().updateOne(Filters.eq("_id", user.getName()), Updates.combine(updateDocument)).wasAcknowledged();

	}

	public long update(List<User> users) 
	{
		long result = 0;
		for(User user : users)
			if(update(user))
				result++;
		return result;
	}
	
	public PojoCursor<User> find(String username, boolean partial)
	{
		FindIterable<User> cursor;
		if(username.isEmpty())
			cursor = getCollection().find();
		else 
		{
			if(partial)
				cursor = getCollection().find(Filters.regex("_id", "/^"+username+"/i"));
			else
				cursor = getCollection().find(Filters.eq("_id", username));
		}
			
		return new PojoCursor<User>(cursor.cursor());
	}
	public User find(String username) 
	{
		return find(username, false).first();
	}
	
	public PojoCursor<User> find()
	{
		return find("", false);
	}
	
	public void drop() 
	{
		getCollection().drop();
	}
}
