package app.scraper.net;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
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
	/*private long lastRequestMillis = 0;
	private final int maxRequestsPerSecond = 3;
	private final double requestMargin = 0.25;
	private boolean additionalRateLimit;*/
	
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
		//TODO: remove the following 3 lines and the client from below
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

		Gson gson = new GsonBuilder().registerTypeAdapter(APICandle.class, new CandleDeserializer()).create();
		retrofit = new Retrofit.Builder().baseUrl("https://api.pro.coinbase.com/").client(client).addConverterFactory(GsonConverterFactory.create(gson)).build();
		apiInterface = retrofit.create(CoinbaseInterface.class);
	}
	
	private void rateLimit() throws InterruptedException
	{
		Thread.sleep(3000);
		/*if(lastRequestMillis == 0) return;
		long curMillis = System.currentTimeMillis();
		double waitTime = (curMillis - lastRequestMillis) - 1/(maxRequestsPerSecond * (1 - requestMargin));
		if (waitTime > 0)
			Thread.sleep((long)Math.ceil(waitTime) + (additionalRateLimit ? 1000 : 0));
		additionalRateLimit = false;
		lastRequestMillis = curMillis;*/
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
		//TODO: check response code
		return response.body();
		
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
		//TODO: check response code
		return response.body();
	}

	public List<APICandle> getCandles(String marketId, int granularity, Instant start) throws InterruptedException
	{
		if(start == null)
			start = findFirstInstant(marketId, granularity);
		if (!start.isBefore(Instant.now()))
			return new ArrayList<APICandle>();
		Instant end = start.plusSeconds(granularity * 60 * 300);
		if (end.isAfter(Instant.now()))
			end = Instant.now();
		List<APICandle> retCandles = getCandles(marketId, granularity, start, end);
		if (retCandles == null || retCandles.isEmpty())
			return new ArrayList<APICandle>();

		List<APICandle> candles = new ArrayList<APICandle>();
		int index = retCandles.size() - 1;
		for (Instant curTime = start; curTime.isBefore(end); curTime = curTime.plusSeconds(granularity * 60)) {
			APICandle curCandle = retCandles.get(Math.max(index, 0));
			Instant curCandleTime = curCandle.getTime();
			if (curCandleTime.isBefore(curTime) && index >= 0)
				throw new RuntimeException("Source returned an out-of-bucket candle (candle time: " + curCandleTime + " | bucket time: " + curTime + ").");
			if (curCandleTime.isAfter(curTime) || index < 0) {
				double value = index < 0 ? curCandle.getClose() : curCandle.getOpen();
				candles.add(new APICandle(curTime, value));
				continue;
			}
			candles.add(curCandle);
			index--;
		}
		
		return candles;
	}
	
	protected Instant findFirstInstant(String marketId, int granularity) throws InterruptedException
	{
		Instant start = Instant.ofEpochSecond(1437428220);
		List<APICandle> curCandles = null;
		while (curCandles == null || curCandles.isEmpty()) {
			if(start.isAfter(Instant.now()))
				return Instant.now();
			Instant end = start.plusSeconds(1440 * 60 * 300);
			curCandles = getCandles(marketId, 1440, start, end);
			start = end.plusSeconds(1);
		}
	
		start = curCandles.get(curCandles.size()-1).getTime();
		curCandles.clear();
		
		while (curCandles.size() == 0) {
			Instant end = start.plusSeconds(granularity * 60 * 300);
			curCandles = getCandles(marketId, granularity, start, end);
			start = end.plusSeconds(1);
		}
		
		return curCandles.get(curCandles.size()-1).getTime();	 
	} 
	
}
