package scraper.entities.mongo;

import java.util.ArrayList;

import org.bson.Document;

import com.google.gson.JsonArray;

public class DBManager {
	private static DBManager instance;
	
	private DBManager()
	{
		
	}
	
	public static DBManager getInstance() 
	{
		if(instance == null)
			instance = new DBManager();
		return instance;
	}
	
	public ArrayList<Document> getDataSources()
	{
		return null;
	}
	
	public void addMarkets(Document...documents) {}
	
	public void removeMarkets(String...strings) {}
	
	public void updateMarket(String name, Document doc) {}
	
	public void addCandles(String name, Document...documents) {}
	
	
}
