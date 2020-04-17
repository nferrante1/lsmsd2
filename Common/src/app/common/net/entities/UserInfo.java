package app.common.net.entities;

public class UserInfo extends Entity {

	private String username;
	private boolean isAdmin;
	
	public UserInfo() {}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
