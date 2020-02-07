package app.scraper.net;

import java.util.List;

import app.scraper.data.Market;
import retrofit2.Call;
import retrofit2.http.GET;


public interface CoinbaseInterface
{
	@GET("products")
	Call<List<Market>> listMarkets();
	//Call<List<Candle>> getCandles(@Path("market") String marketId, @QueryMap)
}
