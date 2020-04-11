package app.common.net;

import java.io.Serializable;

public class RequestLogin extends RequestMessage implements Serializable{
	
	protected String username;
	protected String password;
	
	public RequestLogin(ActionRequest action, String username, String password) {
		
		super(action);
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
