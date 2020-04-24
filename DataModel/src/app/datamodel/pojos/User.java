package app.datamodel.pojos;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

@CollectionName("Users")
public class User extends StorablePojo
{
	@BsonId
	protected String username;
	protected String passwordHash;
	protected boolean admin;

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

	protected final static String hashPassword(String password)
	{
		String passwordHash;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < bytes.length; i++)
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			passwordHash = sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			passwordHash = "";
		}
		return passwordHash;
	}

	public static boolean validatePassword(String password)
	{
		return (password != null && password.length() > 7);
	}

	public static boolean validateUsername(String username)
	{
		return (username != null && username.matches("^[A-Za-z0-9]{3,32}$"));
	}

	public boolean hasValidUsername()
	{
		return validateUsername(username);
	}

	public boolean checkPassword(String password)
	{
		return (this.passwordHash != null && this.passwordHash.equals(hashPassword(password)));
	}

	public boolean checkPasswordHash(String passwordHash)
	{
		return (this.passwordHash != null && this.passwordHash.equals(passwordHash));
	}

	public boolean hasValidPassword()
	{
		return validatePasswordHash(this.passwordHash);
	}

	public static boolean validatePasswordHash(String passwordHash)
	{
		return (passwordHash != null && passwordHash.matches("^[a-fA-F0-9]{64}$"));
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

	public void promote()
	{
		setAdmin(true);
	}

	public void demote()
	{
		setAdmin(false);
	}
}
