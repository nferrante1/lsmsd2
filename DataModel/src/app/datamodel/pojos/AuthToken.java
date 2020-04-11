package app.datamodel.pojos;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import com.google.gson.annotations.SerializedName;


public class AuthToken extends Pojo {
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	@BsonId
	protected String id;
	protected String username;
	protected Instant expireTime;
	
	//Recupero di un token dal DB
	public AuthToken() 
	{
		super();
	}
	
	//Creazione di un nuovo Token
	public AuthToken(String username, boolean isAdmin)
	{
		super(PojoState.STAGED);
		String prefix = isAdmin? "0" : "1";
		this.username = username;
		this.expireTime = Instant.now().plusSeconds(24 * 60 * 60);
		this.id = prefix + generateToken();
	}
	
	//Come si genera?
	private static String generateToken()
	{
		String token;
		byte[] array = new byte[16]; // length is bounded by 7
		new Random().nextBytes(array);
		token = bytesToHex(array);
		System.out.println(token);
		    
		return token;
	}
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		updateField("id", id);
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		updateField("username", username);
	}
	public Instant getExpireTime()
	{
		return expireTime;
	}
	public void setExpireTime(Instant expireTime)
	{
		updateField("expireTime", expireTime);
	}
	
	@BsonIgnore
	public boolean isAdmin()
	{
		return (id.charAt(0) == '0');
	}
}
