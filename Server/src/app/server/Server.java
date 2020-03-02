package app.server;

import java.util.List;

import app.datamodel.Candle;
import app.datamodel.DataRangeCache;
import app.datamodel.DataSource;
import app.datamodel.Market;
import app.datamodel.MarketData;
import app.datamodel.Strategy;
import app.datamodel.mongo.DBManager;

public class Server {

	public static void main(String[] args)
	{
		setupDBManager();

		List<Market> markets = Market.load("COINBASE", 2, 5);
		for (Market market: markets)
			System.out.println(market.getId());
		
		
	}
	
	public static void setupDBManager()
	{
		DBManager.setHostname("127.0.0.1");
		DBManager.setPort(27017);
		 DBManager.setUsername("root");
		DBManager.setPassword("rootpass");
		DBManager.setDatabase("mydb");
	}
	

}
