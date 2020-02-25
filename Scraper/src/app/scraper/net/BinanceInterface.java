package app.scraper.net;

import java.util.List;
import java.util.Map;

import app.scraper.net.data.APICandle;
import app.scraper.net.data.ExchangeInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface BinanceInterface
{
	@GET("exchangeInfo")
	Call<ExchangeInfo> getExchangeInfo();
	@GET("klines")
	Call<List<APICandle>> getCandles(@QueryMap Map<String, String> options);
}
