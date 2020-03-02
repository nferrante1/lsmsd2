package app.scraper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.datamodel.DataSource;
import app.datamodel.mongo.DBManager;
import app.scraper.net.BinanceConnector;
import app.scraper.net.CoinbaseConnector;
import app.scraper.net.SourceConnector;

public class Scraper
{

	private static Map<String, Class<? extends SourceConnector>> sourceConnectorMap = Map.ofEntries(
			Map.entry("COINBASE", CoinbaseConnector.class),
			Map.entry("BINANCE", BinanceConnector.class)
		);
	private static List<Worker> workers = new ArrayList<Worker>();
		
	
	public static void main(String[] args)
	{
		setupDBManager();
		createWorkers();
		for (Worker worker: workers)
			worker.start();
	}
	
	public static void setupDBManager()
	{
		DBManager.setHostname("127.0.0.1");
		DBManager.setPort(27017);
		 DBManager.setUsername("root");
		DBManager.setPassword("rootpass");
		DBManager.setDatabase("mydb");
	}
	
	private static void createWorkers()
	{
		List<DataSource> sources = DataSource.load();
		for (Map.Entry<String, Class<? extends SourceConnector>> sourceConnector: sourceConnectorMap.entrySet())
		{
			DataSource source = null;
			for (DataSource curSource: sources)
				if (curSource.getName().equals(sourceConnector.getKey()))
					source = curSource;
			if (source == null)
				source = new DataSource(sourceConnector.getKey());
			SourceConnector connector;
			try {
				connector = sourceConnector.getValue().getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				continue;
			}
			workers.add(new Worker(source, connector));
		}
	}

}
