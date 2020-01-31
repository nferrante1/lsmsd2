package scraper.sources;

import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import scraper.db.Candle;

public class CoinbaseCandleConverter implements Converter<ResponseBody, ArrayList<Candle>>
{

	@Override
	public ArrayList<Candle> convert(ResponseBody value) throws IOException
	{
		Gson gson = new Gson(); 
		JsonReader jsonReader = gson.newJsonReader(value.charStream());
		
		JsonParser parser = new JsonParser();
		JsonElement jsonTree = parser.parse(jsonReader);
		JsonArray jarr = jsonTree.getAsJsonArray();
		ArrayList<Candle> lb = new ArrayList<Candle>();
		for (int i = 1; i < jarr.size(); i++) {
			JsonArray jarr_candle = jarr.get(i).getAsJsonArray();
			Instant t = Instant.ofEpochSecond(jarr_candle.get(0).getAsLong());
			double o = jarr_candle.get(1).getAsDouble();
			double h = jarr_candle.get(2).getAsDouble();
			double l = jarr_candle.get(3).getAsDouble();
			double c = jarr_candle.get(4).getAsDouble();
			double v = jarr_candle.get(5).getAsDouble();
			//System.out.println(i + ": " + t);
			Candle candle = new Candle(t, o ,h ,l ,c ,v);
			lb.add(candle);
		}
		
		return lb;
	}
	
}
