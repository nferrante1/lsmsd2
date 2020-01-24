package scraper.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Coinbase implements DataSource {

	@Override
	public ArrayList<Market> getMarkets() {
		ArrayList<Market> markets = new ArrayList<Market>();
		
		Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.pro.coinbase.com/").addConverterFactory(GsonConverterFactory.create()).build();
		CoinbaseInterface api = retrofit.create(CoinbaseInterface.class);
		
		Call<List<CoinbaseMarket>> call = api.listMarkets();
		try {
			Response<List<CoinbaseMarket>> res;
			res = call.execute();
			List<CoinbaseMarket> cbMarkets = res.body();
			
			for(CoinbaseMarket cbMarket: cbMarkets)
				markets.add(cbMarket.toMarket());
			
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
		
		
		return markets;
	}

	//@Override
/*	public ArrayList<Bar> getBars() {
		// TODO Auto-generated method stub
		return null;
	}*/

}
