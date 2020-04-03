package app.datamodel.pojos;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.conversions.Bson;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;

public class User extends Pojo {
	@BsonId
	protected String username;
	protected String passwordHash;
	protected boolean isAdmin;
	protected transient AuthToken token;

	private void createAuthToken()
	{
		token =  new AuthToken(username, isAdmin);
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
