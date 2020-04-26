package app.common.net.entities;

public class AuthTokenInfo extends Entity
{
	private static final long serialVersionUID = 1467121217034301020L;

	private final String authToken;

	public AuthTokenInfo(String authToken)
	{
		this.authToken = authToken;
	}

	public String getAuthToken()
	{
		return authToken;
	}
}
