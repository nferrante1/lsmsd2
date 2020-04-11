package app.common.net;

import java.io.Serializable;

import app.datamodel.pojos.AuthToken;

public class RequestConfigMarket extends RequestMessage  {
	
	public String marketName;
	public int granularity;
	public boolean dataSync;
	public boolean selectable;
	
	
	public RequestConfigMarket(String token, String marketName, int granularity, boolean dataSync, boolean selectable) {
		super(ActionRequest.CONFIG_MARKET, token);
		this.marketName = marketName;
		this.granularity = granularity;
		this.dataSync = dataSync;
		this.selectable = selectable;
		
	}

}
