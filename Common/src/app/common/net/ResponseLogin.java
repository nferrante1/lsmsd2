package app.common.net;

public class ResponseLogin extends ResponseMessage {
	String authToken;
	
	
	public ResponseLogin(String authToken) 
	{
		super();
		this.authToken = authToken;
	}
}
