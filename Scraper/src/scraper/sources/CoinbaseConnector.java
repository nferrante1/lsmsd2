package scraper.sources;

import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import scraper.db.Candle;
import scraper.db.Market;

public class CoinbaseConnector implements SourceConnector {

	@Override
	public ArrayList<Market> getMarkets() {
		ArrayList<Market> markets = new ArrayList<Market>();
		
		Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.pro.coinbase.com/").addConverterFactory(GsonConverterFactory.create()).build();
		CoinbaseInterface api = retrofit.create(CoinbaseInterface.class);
		
		Call<List<CoinbaseMarket>> call = api.listMarkets();
		try {
			Response<List<CoinbaseMarket>> res;
			res = call.execute();
			List<CoinbaseMarket> cbMarkets = res.body();
			
			for(CoinbaseMarket cbMarket: cbMarkets)
				markets.add(cbMarket.toMarket());
			
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
		
		
		return markets;
	}

	@Override
	public ArrayList<Candle> getBars(String id, long start, int granularity) {
		Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.pro.coinbase.com/").addConverterFactory(CoinbaseCandleConverterFactory.create()).build();
		CoinbaseInterface api = retrofit.create(CoinbaseInterface.class);
		Map<String, Long> options = new HashMap<String, Long>();
		options.put("start", start);
		options.put("end", 1580471100L);
		//options.put("end", Instant.now().toEpochMilli() / 1000);
		options.put("granularity", 300L);
		Call<ArrayList<Candle>> call = api.getBars(id, options);
		ArrayList<Candle> lb = new ArrayList<Candle>();
		try {
			Response<ArrayList<Candle>> res;
			res = call.execute();
			System.out.println(res.errorBody().string());
			lb = (ArrayList<Candle>) res.body();
			
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
		
		
		return lb;
		// Aggiungere chiamata api
	}
	
	
	

}
