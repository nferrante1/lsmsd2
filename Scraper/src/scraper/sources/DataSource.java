package scraper.sources;

import java.util.ArrayList;
import java.util.Map;

import scraper.db.Bar;
import scraper.db.Market;

public interface DataSource {
	
	public ArrayList<Market>getMarkets();
	
	public ArrayList<Bar>getBars(String id, Map<String, String> options);
	
}
