package scraper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import scraper.db.Candle;
import scraper.db.Market;
import scraper.sources.CoinbaseConnector;
import scraper.sources.SourceConnector;

public class Scraper {

	public static void main(String[] args) {
		
		SourceConnector ds = new CoinbaseConnector();
		ArrayList<Market> m = ds.getMarkets();
		for(Market market: m)
				System.out.println(market.getId());
		
		
		String market_id = "BTC-USD";
		
		ArrayList<Candle> b = ds.getBars(market_id, 1580467800, 300);
		for(Candle bar: b) {
			System.out.println("t: " + bar.getOpenTime() + " | o: " +
				bar.getOpenPrice() + " | l: " +
				bar.getLowPrice() + " | h: " +
				bar.getHighPrice() + " | c: " +
				bar.getClosePrice() + " | v: " +
				bar.getVolume());
		}
				
	}
}
