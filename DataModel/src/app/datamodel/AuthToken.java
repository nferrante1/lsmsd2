package app.datamodel;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;

import com.google.gson.annotations.SerializedName;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.DataObject;
import app.datamodel.mongo.DataObjectId;

@CollectionName("AuthTokens")
public class AuthToken extends DataObject {
	
	@SerializedName("_id")
	@DataObjectId
	@BsonId
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
