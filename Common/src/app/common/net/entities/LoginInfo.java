package app.common.net.entities;

public class LoginInfo extends Entity
{
	private static final long serialVersionUID = -8607436036739782087L;
	protected String username;
	protected String password;
	
	public LoginInfo(String username, String password)
	{
		this.username =username;
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
