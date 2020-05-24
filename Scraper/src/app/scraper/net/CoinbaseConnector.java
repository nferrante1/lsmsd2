package app.scraper.net;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import app.scraper.net.data.APICandle;
import app.scraper.net.data.APIMarket;
import app.scraper.net.exceptions.PermanentAPIException;
import app.scraper.net.exceptions.TemporaryAPIException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoinbaseConnector implements SourceConnector
{
	private final int[] ACCEPTED_GRANULARITY = { 1, 5, 15, 60, 360, 1440 };
	private Retrofit retrofit;
	private CoinbaseInterface apiInterface;
	private int additionalRateLimit = 0;

	protected int getAcceptedGranularity(int granularity)
	{
		for (int i = ACCEPTED_GRANULARITY.length - 1; i >= 0; i--) {
			if (granularity % ACCEPTED_GRANULARITY[i] == 0)
				return granularity;
		}
		return ACCEPTED_GRANULARITY[0];
	}

	private class CandleDeserializer implements JsonDeserializer<APICandle>
	{
		@Override
		public APICandle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
		{
			JsonArray candle = json.getAsJsonArray();
			return new APICandle(Instant.ofEpochSecond(candle.get(0).getAsLong()),
				candle.get(3).getAsDouble(),
				candle.get(2).getAsDouble(),
				candle.get(1).getAsDouble(),
				candle.get(4).getAsDouble(),
				candle.get(5).getAsDouble());
		}
	}

	public CoinbaseConnector()
	{
		// TODO: remove the following 3 lines and the client from below
		// HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		// interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
		// OkHttpClient client = new
		// OkHttpClient.Builder().addInterceptor(interceptor).build();

		Gson gson = new GsonBuilder().registerTypeAdapter(APICandle.class, new CandleDeserializer()).create();
		retrofit = new Retrofit.Builder().baseUrl("https://api.pro.coinbase.com/")// .client(client)
			.addConverterFactory(GsonConverterFactory.create(gson)).build();
		apiInterface = retrofit.create(CoinbaseInterface.class);
	}

	private void rateLimit() throws InterruptedException
	{
		Thread.sleep(3000 + additionalRateLimit);
	}

	@Override
	public List<APIMarket> getMarkets() throws InterruptedException
	{
		rateLimit();

		Call<List<APIMarket>> call = apiInterface.listMarkets();
		Logger.getLogger(CoinbaseConnector.class.getName()).info("Sending request: " + call.request().url() + ".");

		Response<List<APIMarket>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return response.body();
	}

	protected List<APICandle> getCandles(String marketId, int granularity, Instant start, Instant end)
		throws InterruptedException
	{
		rateLimit();

		Map<String, String> options = new HashMap<String, String>();

		options.put("start", start.toString());
		options.put("end", end.toString());
		options.put("granularity", Integer.toString(granularity * 60));

		Call<List<APICandle>> call = apiInterface.getCandles(marketId, options);
		Logger.getLogger(CoinbaseConnector.class.getName()).info("Sending request: " + call.request().url() + ".");

		Response<List<APICandle>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return response.body();
	}

	protected void checkResponse(Response<?> response)
	{
		if (response.isSuccessful())
			return;
		int code = response.code();
		Logger.getLogger(CoinbaseConnector.class.getName()).warning("Received error code from source: " + code + ".");
		switch (code) {
		case 400:
			throw new PermanentAPIException("Bad Request -- Invalid request format.");
		case 401:
			throw new PermanentAPIException("Unauthorized -- Invalid API Key.");
		case 403:
			throw new PermanentAPIException("Forbidden -- You do not have access to the requested resource.");
		case 404:
			throw new PermanentAPIException("Not Found.");
		case 429:
			additionalRateLimit += 1000;
			throw new TemporaryAPIException("Too Many Requests. Rate limit violated.", 2 * 60 * 1000);
		case 500:
			throw new TemporaryAPIException("Internal Server Error -- We had a problem with our server.", 5 * 60 * 1000);
		}
		if (code > 399 && code < 500)
			throw new PermanentAPIException("Malformed request.");
		throw new TemporaryAPIException("Server error.", 5 * 60 * 1000);
	}

	@Override
	public List<APICandle> getCandles(String marketId, int granularity, Instant start) throws InterruptedException
	{
		granularity = getAcceptedGranularity(granularity);
		if (start == null)
			start = findFirstInstant(marketId, granularity);
		if (!start.isBefore(Instant.now()))
			return new ArrayList<APICandle>();
		Instant end = start.plusSeconds(granularity * 60 * 300);
		if (end.isAfter(Instant.now()))
			end = Instant.now();
		List<APICandle> retCandles = getCandles(marketId, granularity, start, end);
		if (retCandles == null) {
			Logger.getLogger(CoinbaseConnector.class.getName()).warning("getCandles() returned null.");
			return null;
		}
		boolean recursive = false;
		if (retCandles.isEmpty()) {
			Logger.getLogger(CoinbaseConnector.class.getName()).info("No candles in range " + start + " - " + end + ".");
			retCandles = getCandles(marketId, granularity, end);
			if (retCandles == null || retCandles.isEmpty())
				return retCandles;
			recursive = true;
		}
		Logger.getLogger(CoinbaseConnector.class.getName()).info("Found candles in range " + start + " - " + end + ".");

		List<APICandle> candles = new ArrayList<APICandle>();
		end = retCandles.get(0).getTime().plusSeconds(1);
		int index = retCandles.size() - 1;
		for (Instant curTime = start; curTime.isBefore(end); curTime = curTime.plusSeconds(granularity * 60)) {
			APICandle curCandle = retCandles.get(recursive ? 0 : index);
			Instant curCandleTime = curCandle.getTime();
			if (curCandleTime.isBefore(curTime)) {
				Logger.getLogger(CoinbaseConnector.class.getName()).warning("Source returned an out-of-bucket candle (candle time: " + curCandleTime + " | bucket time: " + curTime + ").");
				curTime = curCandleTime;
			}
			if (curCandleTime.isAfter(curTime)) {
				double value = curCandle.getOpen();
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
		Logger.getLogger(CoinbaseConnector.class.getName()).info("Searching first available candle for market " + marketId + ".");
		Instant start = Instant.ofEpochSecond(1437428220);
		List<APICandle> curCandles = null;
		while (curCandles == null || curCandles.isEmpty()) {
			if (start.isAfter(Instant.now()))
				return Instant.now();
			Instant end = start.plusSeconds(1440 * 60 * 300);
			curCandles = getCandles(marketId, 1440, start, end);
			start = end.plusSeconds(1);
		}

		start = curCandles.get(curCandles.size() - 1).getTime();
		curCandles.clear();
		granularity = getAcceptedGranularity(granularity);
		
		while (curCandles.size() == 0) {
			Instant end = start.plusSeconds(granularity * 60 * 300);
			curCandles = getCandles(marketId, granularity, start, end);
			start = end.plusSeconds(1);
		}

		Instant firstCandleTime = curCandles.get(curCandles.size() - 1).getTime();
		Logger.getLogger(CoinbaseConnector.class.getName()).info("Found first candle time: " + firstCandleTime + ".");
		return firstCandleTime;
	}
}
