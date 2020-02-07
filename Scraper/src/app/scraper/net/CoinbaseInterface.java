package app.scraper.net;

import java.util.List;
import java.util.Map;

import app.scraper.data.Candle;
import app.scraper.data.Market;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;


public interface CoinbaseInterface
{
	@GET("products")
	Call<List<Market>> listMarkets();
	@GET("products/{market}/candles")
	Call<List<Candle>> getCandles(@Path("market") String marketId, @QueryMap Map<String, String> options);
}
