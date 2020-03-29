package app.common.net;

import java.util.HashMap;

import app.datamodel.pojos.AuthToken;

public class RequestBrowse extends RequestMessage{
	
	public int numPage;
	//public HashMap<String, Object> filters;
	//QUALI SONO I FILTRI DA APPLICARE PER OGNI BROWSE??
	//COME SI IMPLEMENTA L'INVIO DEI FILTRI???
	
	public RequestBrowse(ActionRequest action, AuthToken token, int numPage) {
		super(action,token);
		this.numPage = numPage;
		
	}

}
