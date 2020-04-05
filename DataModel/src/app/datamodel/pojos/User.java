package app.datamodel.pojos;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.codecs.pojo.annotations.BsonId;

public class User extends Pojo {
	
	@BsonId
	protected String username;
	
	protected String passwordHash;
	
	protected boolean admin;
	
	protected transient AuthToken token;
	
	public User() 
	{
		super();
	}
	
	public User(String username, String password)
	{
		super(PojoState.STAGED);
		this.username = username;
		this.passwordHash = hashPassword(password);
		//MANCA INIZIALIZZARE AUTHTOKEN
	}

//	private void createAuthToken()
//	{
//		token =  new AuthToken(username, isAdmin);
//	}

	@BsonId
	public String getName()
	{
		return this.username;
	}
	
	/**
	 * Returns the password hash of the User.
	 * @return String The password hash.
	 */
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
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, "This Java installation does not support SHA-256 hashing algorithm (required for password hashing).", ex);
			passwordHash = "";
		}
		return passwordHash;
	}

	/**
	 * Validates the password.
	 * @param password The password to validate.
	 * @return True if meets the criteria; False otherwise.
	 */
	public static boolean validatePassword(String password)
	{
		return (password != null && password.length() > 7);
	}

	/**
	 * Validates the username.
	 * @param username The username to validate.
	 * @return True if username is valid; False otherwise.
	 */
	public static boolean validateUsername(String username)
	{
		return (username != null && username.matches("^[A-Za-z0-9]{3,32}$"));
	}

	/**
	 * Checks if the user has a valid username.
	 * @return True if the username is valid; False otherwise.
	 */
	public boolean hasValidUsername()
	{
		return validateUsername(username);
	}

	/**
	 * Check if the given password is the same of this user's password.
	 * @param password The password to check.
	 * @return True if the password is correct; False otherwise.
	 */
	public boolean checkPassword(String password)
	{
		return (this.passwordHash != null && this.passwordHash.equals(hashPassword(password)));
	}

	/**
	 * This method is used to compare an Hash with the one stored inside the User object
	 * @param passwordHash to be checked
	 * @return true if passwordHash is equal to the stored hash
	 */
	public boolean checkPasswordHash(String passwordHash)
	{
		return (this.passwordHash != null && this.passwordHash.equals(passwordHash));
	}

	/**
	 * Checks if the user has a valid password.
	 * @return True if the password is valid; False otherwise.
	 */
	public boolean hasValidPassword()
	{
		return validatePasswordHash(this.passwordHash);
	}

	/**
	 * Checks if the given password hash is valid.
	 * @param passwordHash The password hash to check.
	 * @return True if the password hash is valid; False otherwise.
	 */
	public static boolean validatePasswordHash(String passwordHash)
	{
		return (passwordHash != null && passwordHash.matches("^[a-fA-F0-9]{64}$"));
	}

	
	public boolean isAdmin() 
	{
		return this.admin;
	}
}
