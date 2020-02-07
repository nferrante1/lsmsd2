package app.scraper.net;

import java.util.List;
import java.util.Map;

import app.scraper.data.BinanceExchangeInfo;
import app.scraper.data.Candle;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface BinanceInterface
{
	@GET("exchangeInfo")
	Call<BinanceExchangeInfo> getExchangeInfo();
	@GET("klines")
	Call<List<Candle>> getCandles(@QueryMap Map<String, String> options);
}
