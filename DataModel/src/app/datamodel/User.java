package app.datamodel;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.conversions.Bson;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

@CollectionName("Users")
public class User extends Pojo {
	@SerializedName("_id")
	@BsonId
	protected String username;
	protected String passwordHash;
	protected boolean isAdmin;
	protected transient AuthToken token;
	private static transient PojoManager<User> manager;
	

	public static PojoManager<User> getManager()
	{
		if(manager == null)
			manager = new PojoManager<User>(User.class);
		return manager;
	}
	
	public static User load(String username, String password) 
	{
		List<User> users = manager.find(Filters.and(Filters.eq("_id", username), Filters.eq("passwordHash", password)));
		if(users.isEmpty()) 
			return null;
		User user =  users.get(0);
		user.createAuthToken();
		return user;
	}
	
	
	public static List<User> load(int pageNumber, int perPage)
	{
		return load(null, pageNumber, perPage);
	}	
	
	public static List<User> load(String username, int pageNumber, int perPage)
	{
		Bson filter = null;
		if(username != null)
			filter = Filters.eq("username",username);
		return manager.find(filter, "username", true, pageNumber, perPage);
	}



	private void createAuthToken()
	{
		token =  new AuthToken(username, isAdmin);
		AuthToken.getManager().insert(token);
	}

	public String getName()
	{
		return this.username;
	}
	
	public boolean isAdmin() 
	{
		return this.isAdmin;
	}
}
