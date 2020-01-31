package scraper.sources;
import java.lang.annotation.Annotation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import scraper.db.Candle;


public final class CoinbaseCandleConverterFactory extends Converter.Factory {
	
	public static CoinbaseCandleConverterFactory create() {
	    return new CoinbaseCandleConverterFactory();
	  }
	
	@Override
	  public Converter<ResponseBody, ArrayList<Candle>> responseBodyConverter(Type type, Annotation[] annotations,
	      Retrofit retrofit) {
	    return new CoinbaseCandleConverter();
	  }

}
