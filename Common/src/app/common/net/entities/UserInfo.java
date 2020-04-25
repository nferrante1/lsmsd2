package app.common.net.entities;

public class UserInfo extends Entity
{
	private static final long serialVersionUID = -6433251173793910131L;

	private String username;
	private boolean isAdmin;

	public UserInfo()
	{
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
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin)
	{
		this.isAdmin = isAdmin;
	}
}
