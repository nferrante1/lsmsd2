package scraper.sources;
import java.lang.annotation.Annotation;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import scraper.db.Bar;


public final class CoinbaseBarConverterFactory extends Converter.Factory {
	
	public static CoinbaseBarConverterFactory create() {
	    return new CoinbaseBarConverterFactory();
	  }
	
	@Override
	  public Converter<ResponseBody, List<Bar>> responseBodyConverter(Type type, Annotation[] annotations,
	      Retrofit retrofit) {
	    return new CoinbaseBarConverter();
	  }

}
