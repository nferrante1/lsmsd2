package app.common.net;

import java.io.Serializable;

import app.datamodel.pojos.AuthToken;

public class RequestBrowse extends RequestMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7848764684997776122L;
	protected int numPage;
	protected String filter;

	public RequestBrowse(ActionRequest action, String token, String filter, int numPage) {
		super(action,token);
		this.numPage = numPage;
		this.filter = filter;
		
	}
	
	public int getNumPage() {
		return numPage;
	}

	public void setNumPage(int numPage) {
		this.numPage = numPage;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}


}
