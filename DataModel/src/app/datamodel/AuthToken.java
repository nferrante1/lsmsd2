package app.datamodel;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;

import com.google.gson.annotations.SerializedName;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

@CollectionName("AuthTokens")
public class AuthToken extends Pojo {
	
	@SerializedName("_id")
	@BsonId
	protected String id;
	protected String username;
	protected Instant expireTime;
	
	public AuthToken(String username, boolean isAdmin)
	{
		super();
		this.username = username;
		expireTime = Instant.now().plusSeconds(24 * 60 * 60);
		id = generateToken();
	}
	private static String generateToken()
	{
		return "";
	}	
}
