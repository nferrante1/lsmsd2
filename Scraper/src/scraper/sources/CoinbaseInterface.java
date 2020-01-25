package scraper.sources;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import scraper.db.Bar;

/*
@GET("group/{id}/users")
Call<List<User>> groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);
*/
public interface CoinbaseInterface {
	
	@GET("products")
	Call<List<CoinbaseMarket>> listMarkets();
	@GET("products/{market}/candles")
	Call<List<Bar>> getBars(@Path("market") String id, @QueryMap Map<String, String> options);
	
}
