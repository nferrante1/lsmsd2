package app.scraper.net;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
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

import app.scraper.data.Candle;
import app.scraper.data.Market;
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
	
	private class CandleDeserializer implements JsonDeserializer<Candle>
	{
		@Override
		public Candle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
		{
			JsonArray candle = json.getAsJsonArray();
				return new Candle(
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
		Gson gson = new GsonBuilder().registerTypeAdapter(Candle.class, new CandleDeserializer()).create();
		retrofit = new Retrofit.Builder().baseUrl("https://api.pro.coinbase.com/").client(client).addConverterFactory(GsonConverterFactory.create(gson)).build();
		apiInterface = retrofit.create(CoinbaseInterface.class);
	}
	
	private void rateLimit() throws InterruptedException
	{
		Thread.sleep(1000);
	}

	@Override
	public List<Market> getMarkets() throws InterruptedException
	{
		rateLimit();
		
		Call<List<Market>> call = apiInterface.listMarkets();
		
		Response<List<Market>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return response.body();
		
	}

	@Override
	public List<Candle> getCandles(String marketId, int granularity, Instant start, PullDirection direction) throws InterruptedException
	{
		rateLimit();
		
		Map<String, String> options = new HashMap<String, String>();
		
		Instant startTime;
		Instant endTime;
		switch (direction) {
		case REVERSE:
			startTime = start.minusSeconds(granularity * 60 * 300);
			endTime = start;
			break;
		case FORWARD:
		default:
			startTime = start;
			endTime = start.plusSeconds(granularity * 60 * 300);
		}
		
		options.put("start", startTime.toString());
		options.put("end", endTime.toString());
		options.put("granularity", Integer.toString(granularity * 60));
		
		Call<List<Candle>> call = apiInterface.getCandles(marketId, options);
		
		Response<List<Candle>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return response.body();
	}

}
