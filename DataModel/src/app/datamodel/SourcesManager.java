package app.datamodel;

import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.Market;

public class SourcesManager {
	
	protected static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}

	protected MongoCollection<DataSource> getCollection()
	{
		return getDB().getCollection("Sources", DataSource.class);
	}
	
	public void insert(DataSource dataSource)
	{
		
	}
	
	public void insert(List<DataSource> dataSources) 
	{
		
	}
	
	public boolean delete(DataSource dataSource)
	{
		
	}
	
	public boolean delete(List<DataSource> dataSources) 
	{
		return true;
	}
	
	public boolean update(DataSource dataSource) 
	{
		return true;
	}
	
	public long update(List<DataSource> dataSources) 
	{
		for(DataSource dataSource : dataSources)
			update(dataSource);
		return 0;
	}
	
	public boolean updateMarket(Market market) {return true;}
	
	public void drop() 
	{
		getCollection().drop();
	}
	
	public PojoCursor<DataSource> find(boolean getMarket) //Se false, non si prende i mercati
	{	
		return null;
	}
	
	public Market findMarket(String id) {return null;}
	
	public boolean deleteMarket(Market market) {}
	
	public long deleteMarkets(List<Market> markets) {}	
}
