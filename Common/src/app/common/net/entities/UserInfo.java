package app.common.net.entities;

public final class UserInfo extends Entity
{
	private static final long serialVersionUID = -6433251173793910131L;

	private final String username;
	private final boolean admin;

	public UserInfo(String username, boolean admin)
	{
		this.username = username;
		this.admin = admin;
	}

	public String getUsername()
	{
		return username;
	}

	public boolean isAdmin()
	{
		return admin;
	}
}
