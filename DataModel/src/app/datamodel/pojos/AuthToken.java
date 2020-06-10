package app.datamodel.pojos;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("AuthTokens")
public final class AuthToken extends StorablePojo
{
	@BsonId
	private String id;
	private String username;
	private Instant expireTime;

	public AuthToken()
	{
		super();
	}

	AuthToken(String username, boolean isAdmin)
	{
		super(StorablePojoState.UNTRACKED);
		this.username = username;
		this.expireTime = Instant.now().plusSeconds(24 * 60 * 60);
		this.id = generateToken(isAdmin);
	}

	private static String generateToken(boolean isAdmin)
	{
		final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
		byte[] bytes = new byte[16];

		SecureRandom random;
		try {
			random = SecureRandom.getInstance("NativePRNGNonBlocking");
		} catch (NoSuchAlgorithmException e) {
			random = new SecureRandom();
		}
		random.nextBytes(bytes);

		char[] hexChars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			hexChars[i * 2] = HEX_ARRAY[v >>> 4];
			hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}

		if (isAdmin) {
			hexChars[0] = '0';
		} else {
			int i = 0;
			while (hexChars[i] == '0')
				i++;
			hexChars[0] = i < hexChars.length ? hexChars[i] : '1';
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
		return id.charAt(0) == '0';
	}
}
