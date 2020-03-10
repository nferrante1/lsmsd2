package app.server;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import app.datamodel.Candle;
import app.datamodel.Config;
import app.datamodel.ConfigCodec;
import app.datamodel.DataRangeCache;
import app.datamodel.DataSource;
import app.datamodel.Market;
import app.datamodel.MarketData;
import app.datamodel.Report;
import app.datamodel.Strategy;
import app.datamodel.StrategyRun;
import app.datamodel.mongo.DBManager;

public class Server {

	public static void main(String[] args)
	{
		setupDBManager();

		/*Strategy s = new Strategy("aaaa", "bbbbb", null);
		Config c = new Config("COINBASE:BTC-USD", false, 10, Instant.now(), Instant.now());
		c.setParameter("test", "prova");
		c.setParameter("pippo", 10);
		c.setParameter("pluto", Instant.now());
		Report r = new Report(1.4, 3.65, 2.63, 1.1, 562, 63, 11, 16, 5.1, 5.55, 10.1);
		StrategyRun sr = new StrategyRun("ccccc", c, r);
		s.addRun(sr);
		Strategy.getManager().save(Arrays.asList(s));
		s = Strategy.load(0, 3).get(0);*/
		DataSource ds = new DataSource()
		Gson gson = new Gson();
	
		System.out.println(gson.toJson(s));
		//System.out.println(s.getRun(0).getConfig().getParameter("pippo"));
	}
	
	public static void setupDBManager()
	{
		DBManager.setHostname("127.0.0.1");
		DBManager.setPort(27017);
		 DBManager.setUsername("root");
		DBManager.setPassword("rootpass");
		DBManager.setDatabase("mydb");
		DBManager.addCodec(new ConfigCodec());
	}
	

}
