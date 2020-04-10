package app.common.net;

import app.datamodel.pojos.AuthToken;

public class RequestUser extends RequestMessage {
	private String username;
	private String password;
	
	public RequestUser(ActionRequest action,String token, String username, String password)
	{
		super(action, token);
		this.username = username;
		this.password = password;
	}
	
	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

}


