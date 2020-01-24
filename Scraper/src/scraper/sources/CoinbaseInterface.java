package scraper.sources;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CoinbaseInterface {
	
	@GET("products")
	Call<List<CoinbaseMarket>> listMarkets();

}
