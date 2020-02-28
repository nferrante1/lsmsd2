package app.server.datamodel;

import java.util.List;

import org.bson.conversions.Bson;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;

import app.server.datamodel.mongo.CollectionName;
import app.server.datamodel.mongo.DataObject;
import app.server.datamodel.mongo.DataObjectId;

@CollectionName("Users")
public class User extends DataObject {
	@SerializedName("_id")
	@DataObjectId
	protected String username;
	protected String passwordHash;
	protected boolean isAdmin;
	protected transient AuthToken token;
	
	public static User load(String username, String password) 
	{
		List<User> users = load(User.class, Filters.and(Filters.eq("_id", username), Filters.eq("passwordHash", password)));
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
		return load(User.class, filter, "username", true, pageNumber, perPage);
	}



	private void createAuthToken()
	{
		token =  new AuthToken(username, isAdmin);
		token.save();
	}
}
