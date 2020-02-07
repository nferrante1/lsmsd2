package app.scraper.net;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import app.scraper.data.BinanceExchangeInfo;
import app.scraper.data.Candle;
import app.scraper.data.Market;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BinanceConnector implements SourceConnector
{
	private Retrofit retrofit;
	private BinanceInterface apiInterface;
	
	public BinanceConnector()
	{
		retrofit = new Retrofit.Builder().baseUrl("https://api.binance.com/api/v3/").addConverterFactory(GsonConverterFactory.create()).build();
		apiInterface = retrofit.create(BinanceInterface.class);
	}
	
	@Override
	public List<Market> getMarkets()
	{
		Call<BinanceExchangeInfo> call = apiInterface.getExchangeInfo();
		
		Response<BinanceExchangeInfo> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return response.body().symbols;
	}

	@Override
	public List<Candle> getCandles(String marketId, int granularity, Instant start, PullDirection direction)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
