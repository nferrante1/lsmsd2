package app.common.net.entities;

public class UserInfo extends Entity
{
	private static final long serialVersionUID = -6433251173793910131L;

	private String username;
	private boolean admin;

	public UserInfo()
	{
	}
	
	public UserInfo(String username, boolean admin)
	{
		this.username = username;
		this.admin = admin;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public boolean isAdmin()
	{
		return admin;
	}

	public void setAdmin(boolean isAdmin)
	{
		this.admin = isAdmin;
	}
}
