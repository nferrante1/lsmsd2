package app.common.net;

public class ResponseLogin extends ResponseMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6340687743696643468L;
	String authToken;
	
	
	public ResponseLogin(String authToken) 
	{
		super();
		this.authToken = authToken;
	}


	public String getAuthToken()
	{
		return authToken;
	}


	public void setAuthToken(String authToken)
	{
		this.authToken = authToken;
	}
	
}
