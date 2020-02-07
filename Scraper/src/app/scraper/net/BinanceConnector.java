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

import app.scraper.data.BinanceExchangeInfo;
import app.scraper.data.Candle;
import app.scraper.data.Market;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BinanceConnector implements SourceConnector
{
	private Retrofit retrofit;
	private BinanceInterface apiInterface;
	
	private class CandleDeserializer implements JsonDeserializer<Candle>
	{
		@Override
		public Candle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
		{
			JsonArray candle = json.getAsJsonArray();
			return new Candle(
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
		Gson gson = new GsonBuilder().registerTypeAdapter(Candle.class, new CandleDeserializer()).create();
		retrofit = new Retrofit.Builder().baseUrl("https://api.binance.com/api/v3/").addConverterFactory(GsonConverterFactory.create(gson)).build();
		apiInterface = retrofit.create(BinanceInterface.class);
	}
	
	@Override
	public List<Market> getMarkets()
	{
		Call<BinanceExchangeInfo> call = apiInterface.getExchangeInfo();
		
		Response<BinanceExchangeInfo> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return response.body().symbols;
	}
	
	private String getIntervalString(int minutes)
	{
		if (minutes == 60 * 24 * 7)
			return "1w";
		if (minutes >= 60 * 24)
			return (minutes / (60 * 24)) + "d";
		if (minutes >= 60)
			return (minutes / 60) + "h";
		return minutes + "m";
	}

	@Override
	public List<Candle> getCandles(String marketId, int granularity, Instant start, PullDirection direction)
	{
		Map<String, String> options = new HashMap<String, String>();
		
		options.put("symbol", marketId);
		switch (direction) {
		case REVERSE:
			options.put("endTime", Long.toString(start.getEpochSecond() * 1000));
			break;
		case FORWARD:
		default:
			options.put("startTime", Long.toString(start.getEpochSecond() * 1000));
		}
		options.put("interval", getIntervalString(granularity));
		options.put("limit", "1000");
		
		Call<List<Candle>> call = apiInterface.getCandles(options);
		
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
