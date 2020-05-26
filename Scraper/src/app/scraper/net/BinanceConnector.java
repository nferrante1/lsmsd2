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
import app.scraper.net.data.ExchangeInfo;
import app.scraper.net.exceptions.PermanentAPIException;
import app.scraper.net.exceptions.TemporaryAPIException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class BinanceConnector implements SourceConnector
{
	private final int[] ACCEPTED_GRANULARITY = { 1, 3, 5, 15, 30, 60, 120, 240, 360, 480, 720, 1440, 4320, 10080 };
	private final Retrofit retrofit;
	private final BinanceInterface apiInterface;
	private int additionalRateLimit = 0;

	private int getAcceptedGranularity(int granularity)
	{
		for (int i = ACCEPTED_GRANULARITY.length - 1; i >= 0; i--) {
			if (granularity % ACCEPTED_GRANULARITY[i] == 0)
				return ACCEPTED_GRANULARITY[i];
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
			return new APICandle(Instant.ofEpochMilli(candle.get(0).getAsLong()),
				Double.parseDouble(candle.get(1).getAsString()),
				Double.parseDouble(candle.get(2).getAsString()),
				Double.parseDouble(candle.get(3).getAsString()),
				Double.parseDouble(candle.get(4).getAsString()),
				Double.parseDouble(candle.get(5).getAsString()));
		}
	}

	public BinanceConnector()
	{
		Gson gson = new GsonBuilder().registerTypeAdapter(APICandle.class, new CandleDeserializer()).create();
		retrofit = new Retrofit.Builder().baseUrl("https://api.binance.com/api/v3/")
			.addConverterFactory(GsonConverterFactory.create(gson)).build();
		apiInterface = retrofit.create(BinanceInterface.class);
	}

	private void rateLimit() throws InterruptedException
	{
		Thread.sleep(3000 + additionalRateLimit);
	}

	@Override
	public List<APIMarket> getMarkets() throws InterruptedException
	{
		rateLimit();
		Call<ExchangeInfo> call = apiInterface.getExchangeInfo();
		Logger.getLogger(BinanceConnector.class.getName()).info("Sending request: " + call.request().url() + ".");

		Response<ExchangeInfo> response;
		try {
			response = call.execute();
		} catch (IOException | RuntimeException e) {
			throw new TemporaryAPIException("Unexpected error.", e, 5 * 60 * 1000);
		}

		checkResponse(response);

		ExchangeInfo body = response.body();
		return body == null ? null : body.getMarkets();
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

	private List<APICandle> _getCandles(String marketId, int granularity, Instant start)
		throws InterruptedException
	{
		rateLimit();
		Map<String, String> options = new HashMap<String, String>();

		options.put("symbol", marketId);
		if (start == null)
			options.put("startTime", "0");
		else
			options.put("startTime", Long.toString(start.getEpochSecond() * 1000));
		options.put("interval", getIntervalString(granularity));
		options.put("limit", "1000");

		Call<List<APICandle>> call = apiInterface.getCandles(options);
		Logger.getLogger(BinanceConnector.class.getName()).info("Sending request: " + call.request().url() + ".");

		Response<List<APICandle>> response;
		try {
			response = call.execute();
		} catch (IOException | RuntimeException e) {
			throw new TemporaryAPIException("Unexpected error.", e, 5 * 60 * 1000);
		}

		checkResponse(response);

		return response.body();
	}

	private void checkResponse(Response<?> response)
	{
		if (response.isSuccessful())
			return;
		int code = response.code();
		Logger.getLogger(BinanceConnector.class.getName()).warning("Received error code from source: " + code + ".");
		switch (code) {
		case 403:
			throw new TemporaryAPIException("WAF Limit vuiolated.", 20 * 60 * 1000);
		case 429:
			additionalRateLimit += 1000;
			throw new TemporaryAPIException("Rate limit violated.", 2 * 60 * 1000);
		case 418:
			additionalRateLimit += 10000;
			throw new TemporaryAPIException("Rate limit violated. Banned by the source.", 24 * 60 * 60 * 1000);
		case 504:
			throw new TemporaryAPIException("Unknown error.");
		}
		if (code > 399 && code < 500)
			throw new PermanentAPIException("Malformed request.");
		throw new TemporaryAPIException("Server error.", 5 * 60 * 1000);
	}

	@Override
	public List<APICandle> getCandles(String marketId, int granularity, Instant start) throws InterruptedException
	{
		granularity = getAcceptedGranularity(granularity);
		if (start != null && !start.isBefore(Instant.now()))
			return new ArrayList<APICandle>();
		List<APICandle> retCandles = _getCandles(marketId, granularity, start);
		if (retCandles == null || retCandles.isEmpty()) {
			Logger.getLogger(BinanceConnector.class.getName()).info("No candles found after " + start + ".");
			return retCandles;
		}

		List<APICandle> candles = new ArrayList<APICandle>();
		if (start == null)
			start = retCandles.get(0).getTime();
		Instant end = retCandles.get(retCandles.size() - 1).getTime().plusSeconds(1);
		int index = 0;
		for (Instant curTime = start; curTime.isBefore(end); curTime = curTime.plusSeconds(granularity * 60)) {
			APICandle curCandle = retCandles.get(index);
			Instant curCandleTime = curCandle.getTime();
			if (curCandleTime.isBefore(curTime)) {
				Logger.getLogger(BinanceConnector.class.getName()).warning("Source returned an out-of-bucket candle (candle time: " + curCandleTime + " | bucket time: " + curTime + ").");
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
