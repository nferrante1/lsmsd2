package app.datamodel.pojos;

import java.time.Instant;

public class DataRange  extends Pojo {
	
	public Instant start;
	public Instant end;
	
	public DataRange() 
	{	
		
	}
	
	public DataRange( Instant start, Instant end) {
;
		this.start = start;
		this.end = end;
	}
}
