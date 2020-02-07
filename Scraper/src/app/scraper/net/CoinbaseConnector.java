package app.scraper.net;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import app.scraper.data.Candle;
import app.scraper.data.Market;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoinbaseConnector implements SourceConnector
{
	private Retrofit retrofit;
	private CoinbaseInterface apiInterface;
	
	public CoinbaseConnector()
	{
		retrofit = new Retrofit.Builder().baseUrl("https://api.pro.coinbase.com/").addConverterFactory(GsonConverterFactory.create()).build();
		apiInterface = retrofit.create(CoinbaseInterface.class);
	}

	@Override
	public List<Market> getMarkets()
	{
		Call<List<Market>> call = apiInterface.listMarkets();
		
		Response<List<Market>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return response.body();
		
	}

	@Override
	public List<Candle> getCandles(String marketId, int granularity, Instant start, PullDirection direction)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
