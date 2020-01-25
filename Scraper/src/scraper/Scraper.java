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

import scraper.db.Bar;
import scraper.db.Market;
import scraper.sources.Coinbase;
import scraper.sources.DataSource;

public class Scraper {

	public static void main(String[] args) {
		
		DataSource ds = new Coinbase();
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
		
		
		ArrayList<Bar> b = ds.getBars(market_id, options);
		for(Bar bar: b) {
			System.out.println(bar.t);
			System.out.println(bar.o);
			System.out.println(bar.l);
			System.out.println(bar.h);
			System.out.println(bar.c);
			System.out.println(bar.v);
		}
				
	}
}
