package app.common.net;

import java.time.YearMonth;

import app.datamodel.AuthToken;

public class RequestDeleteData extends RequestMessage {
	
		
		public YearMonth start;
		public YearMonth end;
		public String name;
		
		
		public RequestDeleteData(AuthToken token, YearMonth start, YearMonth end, String name) 
		{
			super(ActionRequest.DELETE_DATA, token);
			this.start = start;
			this.end = end;
			this.name = name;
		}
		
		public RequestDeleteData(AuthToken token, YearMonth start, YearMonth end) 
		{
			this(token,start,end,null);
		}
		
		public RequestDeleteData(AuthToken token,String name) 
		{
			this(token,null,null,name);
		}
		
		public RequestDeleteData(AuthToken token) 
		{
			this(token,null,null,null);
		}
		
		
}
