package app.scraper.net;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import app.scraper.net.data.APICandle;
import app.scraper.net.data.APIMarket;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoinbaseConnector implements SourceConnector
{
	private Retrofit retrofit;
	private CoinbaseInterface apiInterface;
	private long lastRequestMillis = 0;
	private final int maxRequestsPerSecond = 3;
	private final double requestMargin = 0.25;
	private boolean additionalRateLimit;
	private List<APICandle> candles;
	
	private class CandleDeserializer implements JsonDeserializer<APICandle>
	{
		@Override
		public APICandle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
		{
			JsonArray candle = json.getAsJsonArray();
				return new APICandle(
					Instant.ofEpochSecond(candle.get(0).getAsLong()),
					candle.get(1).getAsDouble(),
					candle.get(2).getAsDouble(),
					candle.get(3).getAsDouble(),
					candle.get(4).getAsDouble(),
					candle.get(5).getAsDouble()
				);
		}
		
	}
	
	public CoinbaseConnector()
	{
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
		Gson gson = new GsonBuilder().registerTypeAdapter(APICandle.class, new CandleDeserializer()).create();
		retrofit = new Retrofit.Builder().baseUrl("https://api.pro.coinbase.com/").client(client).addConverterFactory(GsonConverterFactory.create(gson)).build();
		apiInterface = retrofit.create(CoinbaseInterface.class);
		candles = new ArrayList<APICandle>();
	}
	
	private void rateLimit() throws InterruptedException
	{
		Thread.sleep(3000);
		if(lastRequestMillis == 0) return;
		long curMillis = System.currentTimeMillis();
		double waitTime = (curMillis - lastRequestMillis) - 1/(maxRequestsPerSecond * (1 - requestMargin));
		if (waitTime > 0)
			Thread.sleep((long)Math.ceil(waitTime) + (additionalRateLimit ? 1000 : 0));
		additionalRateLimit = false;
		lastRequestMillis = curMillis;
	}

	@Override
	public List<APIMarket> getMarkets() throws InterruptedException
	{
		rateLimit();
		
		Call<List<APIMarket>> call = apiInterface.listMarkets();
		
		Response<List<APIMarket>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return response.body();
		
	}

	@Override
	public List<APICandle> getLastCandles(String marketId, int granularity, Instant start) throws InterruptedException
	{
		return getCandles(marketId, granularity, start);
	}

	protected List<APICandle> getCandles(String marketId, int granularity, Instant start) throws InterruptedException
	{
		return getCandles(marketId, granularity, start, start.plusSeconds(granularity * 60 * 300));
	}
	
	protected List<APICandle> getCandles(String marketId, int granularity, Instant start, Instant end) throws InterruptedException
	{
		rateLimit();
		
		Map<String, String> options = new HashMap<String, String>();
		
		options.put("start", start.toString());
		options.put("end", end.toString());
		options.put("granularity", Integer.toString(granularity * 60));
		
		Call<List<APICandle>> call = apiInterface.getCandles(marketId, options);
		
		Response<List<APICandle>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return response.body();
	}
/*
	@Override
	public List<APICandle> getMonthCandles(String marketId, int granularity, YearMonth month)
		throws InterruptedException
	{
		List<APICandle> candles = new ArrayList<APICandle>();
		Instant startMonth = month.atDay(1).atStartOfDay(ZoneId.of("UTC")).toInstant();
		Instant lastMonth = (month.equals(YearMonth.now())) ? Instant.now() : month.atEndOfMonth().atTime(23, 59, 59, 999999999).atZone(ZoneId.of("UTC")).toInstant();
		Instant start = startMonth;
		while (start.isBefore(lastMonth)) {
			Instant end = start.plusSeconds(granularity * 60 * 300);
			if (end.isAfter(lastMonth))
				end = lastMonth;
			List<APICandle> curCandles = getCandles(marketId, granularity, start, end);
			Collections.reverse(curCandles);
			candles.addAll(curCandles);
			start = end.plusSeconds(1);
		}
		return candles;
	}
	*/
	public List<APICandle> getThousandCandles(String marketId, int granularity, YearMonth month)
		throws InterruptedException
	{
		List<APICandle> returnCandles = new ArrayList<APICandle>();
		Instant startMonth = month.atDay(1).atStartOfDay(ZoneId.of("UTC")).toInstant();
		Instant start = startMonth;
		while (candles.size() < 1000) {
			Instant end = start.plusSeconds(granularity * 60 * 300);
			List<APICandle> curCandles = getCandles(marketId, granularity, start, end);
			Collections.reverse(curCandles);
			candles.addAll(curCandles);
			start = end.plusSeconds(1);
		}
		
		for(int i = 0; i < 1000; i++) {
			returnCandles.add(i, candles.get(0));
			candles.remove(0);
		}
		
		
		return returnCandles;
	}


}
