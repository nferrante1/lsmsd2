package app.common.net.entities;

public class SourceInfo extends Entity {

	private String _id;
	private boolean enabled;
	
	public SourceInfo() {}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}