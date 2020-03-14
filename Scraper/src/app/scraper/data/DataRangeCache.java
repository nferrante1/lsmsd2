package app.scraper.data;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataRangeCache {
	private static DataRangeCache instance;
	private List<DataRange> ranges = new ArrayList<DataRange>() ;
	
	private DataRangeCache() {
		cacheRanges();
	};
	
	public static synchronized DataRangeCache getInstance() 
	{
		if (instance == null)
			instance = new DataRangeCache();
		return instance;
	}
	
	public List<DataRange> getRanges()
	{
		return this.ranges;
	}
	
	public void setRanges(List<DataRange> ranges)
	{
		this.ranges = ranges;
	}
	
	public void cacheRanges()
	{
		this.ranges = (new DataRangeManager()).getRanges();				
	}
	
	public void setStartMonth(String marketId, YearMonth newMonth) 
	{
		setMonth(marketId, newMonth, true);
	}
	
	public void setEndMonth(String marketId, YearMonth newMonth) 
	{
		setMonth(marketId, newMonth, false);
	}
	
	private void setMonth(String marketId, YearMonth newMonth, boolean start)
	{
		for(DataRange range: ranges) {
			if(range.id.equals(marketId)) {
				if(start)
					range.start = newMonth;
				else
					range.end = newMonth;
				return;
			}
		}
		ranges.add(new DataRange(marketId, newMonth, newMonth));
	}
	
	public YearMonth getStartMonth(String marketId)
	{
		return getMonth(marketId, true);
	}
	
	public YearMonth getEndMonth(String marketId)
	{
		return getMonth(marketId, false);
	}
	
	private YearMonth getMonth(String marketId, boolean start)
	{

		for(DataRange range: ranges) {
			if(range.id.equals(marketId))
				if(start)
					 return range.start;
			return range.end;
		}
		
		return null;
	}

	public DataRange getRange(String id)
	{
		for(DataRange range: ranges) {
			if(range.id.equals(id))
				return range;
		}
		return null;
	}
}
