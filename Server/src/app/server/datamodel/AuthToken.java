package app.server.datamodel;

import java.time.Instant;

import com.google.gson.annotations.SerializedName;

import app.server.datamodel.mongo.CollectionName;
import app.server.datamodel.mongo.DataObject;
import app.server.datamodel.mongo.DataObjectId;

@CollectionName("AuthTokens")
public class AuthToken extends DataObject {
	
	@SerializedName("_id")
	@DataObjectId
	protected String id;
	protected String username;
	protected boolean isAdmin;
	protected Instant expireTime;
	
	public AuthToken(String username, boolean isAdmin)
	{
		super();
		this.username = username;
		this.isAdmin = isAdmin;
		expireTime = Instant.now().plusSeconds(24 * 60 * 60);
		id = generateToken();
	}
	private static String generateToken()
	{
		return "";
	}	
}
