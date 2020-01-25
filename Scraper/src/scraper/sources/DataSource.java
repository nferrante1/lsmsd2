package scraper.sources;

import java.util.ArrayList;

import scraper.db.Bar;
import scraper.db.Market;

public interface DataSource {
	
	public ArrayList<Market>getMarkets();
	
	public ArrayList<Bar>getBars();
	
}
