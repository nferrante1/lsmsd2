package app.server;

import java.util.List;

import app.server.datamodel.Candle;
import app.server.datamodel.DataRangeCache;
import app.server.datamodel.DataSource;
import app.server.datamodel.Market;
import app.server.datamodel.MarketData;
import app.server.datamodel.Strategy;
import app.server.datamodel.mongo.DBManager;

public class Server {

	public static void main(String[] args)
	{
		setupDBManager();

		List<Strategy> strategies = Strategy.load(0, 5);
		strategies.get(0).setName("strategietta");;
		strategies.get(0).save();
		
		
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
