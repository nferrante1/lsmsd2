package app.server;

import java.util.List;

import app.server.datamodel.Candle;
import app.server.datamodel.DataSource;
import app.server.datamodel.Market;
import app.server.datamodel.MarketData;
import app.server.datamodel.mongo.DBManager;

public class Server {

	public static void main(String[] args)
	{
		setupDBManager();
		List<DataSource> sources = DataSource.load();
		for(DataSource source: sources) {
			System.out.println(source.getName());
			List<Market> markets = source.getMarkets();
			for(Market market : markets) {
				System.out.println(market.getId() + market.getBaseCurrency() + market.getQuoteCurrency());
				market.delete();
				break;
			}
		}
		List<MarketData> datas = MarketData.load(MarketData.class);
		for(MarketData data : datas) {
			List<Candle> candles = data.getCandles();
			for(Candle candle : candles) 
			{
				candle.delete();
				break;
				
			}
		}
		
		
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
