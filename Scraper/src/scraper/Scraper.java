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
				System.out.println(market.id);
		
		
		String market_id = "BTC-USD";
		Map<String,String> options = new HashMap<String,String>();
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(tz);
		
		Instant now = Instant.now();
		Instant yesterday = now.minus(1, ChronoUnit.DAYS);
		
		String startAsISO = df.format(Date.from(yesterday));
		String nowAsISO = df.format(Date.from(now));
		
		options.put("start", startAsISO);
		options.put("end", nowAsISO);
		options.put("granularity", "300");
		
		
		ArrayList<Candle> b = ds.getBars(market_id, options);
		for(Candle bar: b) {
			System.out.println(bar.time);
			System.out.println(bar.open);
			System.out.println(bar.low);
			System.out.println(bar.high);
			System.out.println(bar.close);
			System.out.println(bar.volume);
		}
				
	}
}
