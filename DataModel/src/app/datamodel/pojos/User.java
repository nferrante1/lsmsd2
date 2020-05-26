package app.datamodel.pojos;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import org.bson.codecs.pojo.annotations.BsonId;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("Users")
public final class User extends StorablePojo
{
	@BsonId
	private String username;
	private String passwordHash;
	private boolean admin;

	public User()
	{
		super();
	}

	public User(String username, String password, boolean admin)
	{
		super(StorablePojoState.UNTRACKED);
		this.username = username;
		this.passwordHash = hashPassword(password);
		this.admin = admin;
	}

	public User(String username, String password)
	{
		this(username, password, false);
	}

	public String getUsername()
	{
		return this.username;
	}

	public String getPasswordHash()
	{
		return this.passwordHash;
	}

	private final static String hashPassword(String password)
	{
		String passwordHash;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++)
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			passwordHash = sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			passwordHash = password;
			Logger.getLogger(User.class.getName()).severe("Can not hash password: hash function SHA-256 not available.");
		}
		return passwordHash;
	}

	public boolean checkPassword(String password)
	{
		return (this.passwordHash != null && this.passwordHash.equals(hashPassword(password)));
	}

	public boolean isAdmin()
	{
		return this.admin;
	}

	public void setUsername(String username)
	{
		updateField("username", username);
	}

	public void setPasswordHash(String passwordHash)
	{
		updateField("passwordHash", passwordHash);
	}

	public void setAdmin(boolean admin)
	{
		updateField("admin", admin);
	}

	public AuthToken generateToken()
	{
		return new AuthToken(username, admin);
	}
}
