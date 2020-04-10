package app.common.net;

import app.datamodel.pojos.AuthToken;

public class RequestById extends RequestMessage {
	
	protected String id;
	
	public RequestById(ActionRequest action, String token, String id) {
		
		super(action,token);
		this.id =id;
	}

}
