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
	protected boolean isAdmin;
	protected Instant expireTime;
	private static transient PojoManager<AuthToken> manager;
	
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
	
	public static PojoManager<AuthToken> getManager()
	{
		if(manager == null)
			manager = new PojoManager<AuthToken>(AuthToken.class);
		return manager;
	}
}
