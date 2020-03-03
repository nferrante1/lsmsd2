package app.common.net;

import app.datamodel.AuthToken;

public class RequestById extends RequestMessage {
	
	protected String id;
	
	public RequestById(ActionRequest action, AuthToken token, String id) {
		
		super(action,token);
		this.id =id;
	}

}
