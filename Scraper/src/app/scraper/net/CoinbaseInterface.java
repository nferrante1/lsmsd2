package app.scraper.net;

import java.util.List;
import java.util.Map;

import app.scraper.net.data.APICandle;
import app.scraper.net.data.APIMarket;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;


public interface CoinbaseInterface
{
	@GET("products")
	Call<List<APIMarket>> listMarkets();
	@GET("products/{market}/candles")
	Call<List<APICandle>> getCandles(@Path("market") String marketId, @QueryMap Map<String, String> options);
}
