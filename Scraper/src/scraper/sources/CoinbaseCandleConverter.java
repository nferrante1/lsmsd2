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

public class CoinbaseCandleConverter implements Converter<ResponseBody,List<Candle>>{

	@Override
	public List<Candle> convert(ResponseBody value) throws IOException {
		//Per provare creo una lista con un solo elemento
		
		Gson gson = new Gson(); 
		JsonReader jsonReader = gson.newJsonReader(value.charStream());
		
		JsonParser parser = new JsonParser();
		JsonElement jsonTree = parser.parse(jsonReader);
		JsonArray jarr = jsonTree.getAsJsonArray();
		JsonArray jarr_bar = jarr.get(1).getAsJsonArray();
		
		Instant t = Instant.ofEpochSecond(jarr_bar.get(0).getAsLong());
		double o = jarr_bar.get(1).getAsDouble();
		double h = jarr_bar.get(2).getAsDouble();
		double l = jarr_bar.get(3).getAsDouble();
		double c = jarr_bar.get(4).getAsDouble();
		double v = jarr_bar.get(5).getAsDouble();
		
		Candle bar= new Candle(t,o,h,l,c,v);
		List<Candle> lb= new ArrayList<Candle>();
		lb.add(bar);
		
		return lb;}
	
}
