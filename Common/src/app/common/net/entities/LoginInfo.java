package app.common.net.entities;

public final class LoginInfo extends Entity
{
	private static final long serialVersionUID = -8607436036739782087L;

	private final String username;
	private final String password;

	public LoginInfo(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}
}
