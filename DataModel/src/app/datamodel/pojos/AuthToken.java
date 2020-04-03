package app.datamodel.pojos;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;

import com.google.gson.annotations.SerializedName;


public class AuthToken extends Pojo {
	
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
