package app.common.net;

import app.datamodel.AuthToken;

public class LoginResponse extends ResponseMessage {
	AuthToken authToken;
	
	public LoginResponse(String username, boolean isAdmin) 
	{
		super();
		this.authToken = new AuthToken(username, isAdmin);
	}
	
	public LoginResponse(AuthToken authToken) 
	{
		
	}
}
