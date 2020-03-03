package app.common.net;

import app.datamodel.AuthToken;

public class ResponseLogin extends ResponseMessage {
	AuthToken authToken;
	
	public ResponseLogin(String username, boolean isAdmin) 
	{
		super();
		this.authToken = new AuthToken(username, isAdmin);
	}
	
	public ResponseLogin(AuthToken authToken) 
	{
		super();
		this.authToken = authToken;
	}
}
