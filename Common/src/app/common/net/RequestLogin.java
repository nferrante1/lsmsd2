package app.common.net;

public class RequestLogin extends RequestMessage {
	
	protected String username;
	protected String password;
	
	public RequestLogin(ActionRequest action, String token, String username, String password) {
		
		super(action,token);
		this.username =username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


}
