package scraper;

import java.util.ArrayList;
import java.util.Map;

import scraper.api.CoinbaseConnector;

public class Scraper {
	private Map<String,Class<?>> connectors = Map.ofEntries(Map.entry("COINBASE", CoinbaseConnector.class));
	ArrayList<DataSourceHandler> dataSourceHandlers;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
