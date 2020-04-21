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
import app.scraper.net.data.ExchangeInfo;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BinanceConnector implements SourceConnector
{
	private Retrofit retrofit;
	private BinanceInterface apiInterface;
	/*private int lastRequestMinute;
	private int usedWeight;
	private final int availableWeight = 1200;
	private final double requestMargin = 0.25;
	private int requestedWaitTime;*/
	
	private class CandleDeserializer implements JsonDeserializer<APICandle>
	{
		@Override
		public APICandle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
		{
			JsonArray candle = json.getAsJsonArray();
			return new APICandle(
				Instant.ofEpochMilli(candle.get(0).getAsLong()),
				Double.parseDouble(candle.get(1).getAsString()),
				Double.parseDouble(candle.get(2).getAsString()),
				Double.parseDouble(candle.get(3).getAsString()),
				Double.parseDouble(candle.get(4).getAsString()),
				Double.parseDouble(candle.get(5).getAsString())
				);
		}
		
	}
	
	public BinanceConnector()
	{
		//TODO: remove the following 3 lines and client from below
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

		Gson gson = new GsonBuilder().registerTypeAdapter(APICandle.class, new CandleDeserializer()).create();
		retrofit = new Retrofit.Builder().baseUrl("https://api.binance.com/api/v3/").client(client).addConverterFactory(GsonConverterFactory.create(gson)).build();
		apiInterface = retrofit.create(BinanceInterface.class);
	}
	
	private void rateLimit() throws InterruptedException
	{
		Thread.sleep(3000);
		/*LocalTime curTime = LocalTime.now();
		int curMinute = curTime.getHour() * 60 + curTime.getMinute();
		int remainingSeconds = 60 - curTime.getSecond();
		if (lastRequestMinute == curMinute) {
			double curAvailWeight = (availableWeight * (1 - requestMargin)) - usedWeight;
			if (curAvailWeight <= 0)
				Thread.sleep(remainingSeconds);
			else
				Thread.sleep((long)Math.ceil(curAvailWeight / remainingSeconds));
		}
		if (requestedWaitTime > 0)
			Thread.sleep(requestedWaitTime);*/
	}
	
	@Override
	public List<APIMarket> getMarkets() throws InterruptedException
	{
		rateLimit();
		Call<ExchangeInfo> call = apiInterface.getExchangeInfo();
		
		Response<ExchangeInfo> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		/*String weight = response.headers().get("x-mbx-used-weight-1m");
		if (weight != null)
			usedWeight = Integer.parseInt(weight);
		else
			usedWeight++;*/
		//TODO: check response code
		return response.body().getMarkets();
	}
	
	private String getIntervalString(int minutes)
	{
		if (minutes == 60 * 24 * 7)
			return "1w";
		if (minutes >= 60 * 24) {
			if (minutes % 60 * 24 != 0)
				throw new IllegalArgumentException();
			return (minutes / (60 * 24)) + "d";
		}
		if (minutes >= 60) {
			if (minutes % 60 != 0)
				throw new IllegalArgumentException();
			return (minutes / 60) + "h";
		}
		return minutes + "m";
	}

	protected List<APICandle> _getCandles(String marketId, int granularity, Instant start) throws InterruptedException
	{
		rateLimit();
		Map<String, String> options = new HashMap<String, String>();
		
		options.put("symbol", marketId);
		if(start == null)
			options.put("startTime", "0");
		else
			options.put("startTime", Long.toString(start.getEpochSecond() * 1000));
		//options.put("endTime", Long.toString(end.getEpochSecond() * 1000));
		options.put("interval", getIntervalString(granularity));
		options.put("limit", "1000");
		
		Call<List<APICandle>> call = apiInterface.getCandles(options);
		
		Response<List<APICandle>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		/*String weight = response.headers().get("x-mbx-used-weight-1m");
		if (weight != null)
			usedWeight = Integer.parseInt(weight);
		else
			usedWeight++;*/
		//TODO: check response code
		return response.body();
	}

	
	public List<APICandle> getCandles(String marketId, int granularity, Instant start) throws InterruptedException
	{
		if (start != null && !start.isBefore(Instant.now()))
			return new ArrayList<APICandle>();
		/*Instant end = start.plusSeconds(granularity * 60 * 1000);
		if (end.isAfter(Instant.now()))
			end = Instant.now();*/
		List<APICandle> retCandles = _getCandles(marketId, granularity, start);
		if (retCandles == null || retCandles.isEmpty())
			return retCandles;

		List<APICandle> candles = new ArrayList<APICandle>();
		if (start == null)
			start = retCandles.get(0).getTime();
		Instant end = retCandles.get(retCandles.size() - 1).getTime().plusSeconds(1);
		int index = 0;
		for (Instant curTime = start; curTime.isBefore(end); curTime = curTime.plusSeconds(granularity * 60)) {
			APICandle curCandle = retCandles.get(index);
			Instant curCandleTime = curCandle.getTime();
			if (curCandleTime.isBefore(curTime)) {
				//throw new RuntimeException("Source returned an out-of-bucket candle (candle time: " + curCandleTime + " | bucket time: " + curTime + ").");
				curTime = curCandleTime;
			}
			if (curCandleTime.isAfter(curTime)) {
				double value = curCandle.getOpen();
				candles.add(new APICandle(curTime, value));
				continue;
			}
			candles.add(curCandle);
			index++;
		}
		
		return candles;
	}


}
