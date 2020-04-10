package app.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;

import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.Market;
import app.datamodel.pojos.PojoState;
import app.datamodel.pojos.User;

public class SourcesManager {
	
	protected static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}

	protected MongoCollection<DataSource> getCollection()
	{
		return getDB().getCollection("Sources", DataSource.class);
	}
	
	public void save(DataSource dataSource)
	{
		switch(dataSource.getState())
		{
		case STAGED:
			insert(dataSource);
			return;
		case COMMITTED:
			update(dataSource);
			return;
		case REMOVED:
			delete(dataSource);
			return;
		default:
		}
	}
	
	public void insert(DataSource dataSource)
	{
		getCollection().insertOne(dataSource);
		dataSource.setState(PojoState.COMMITTED);
	}
	
	public void insert(List<DataSource> dataSources) 
	{
		getCollection().insertMany(dataSources);
		for(DataSource ds : dataSources) {
			ds.setState(PojoState.COMMITTED);
		}
	}
	
	public boolean delete(DataSource dataSource)
	{
		dataSource.setState(PojoState.REMOVED);
		return getCollection().deleteOne(Filters.eq("_id", dataSource.getName())).wasAcknowledged();
	}
	
	public long delete(List<DataSource> dataSources) 
	{
		long result = 0;
		for(DataSource dataSource : dataSources)
			if(delete(dataSource))
				result++;
		return result;		
	}
	
	public boolean update(DataSource dataSource) 
	{
		
		List<Bson> updateDocument = new ArrayList<Bson>();
		UpdateOptions options = new UpdateOptions();
		List<Bson> arrayFilters = new ArrayList<Bson>();
		List<Market> addedMarkets = new ArrayList<Market>();
		List<Market> removedMarkets = new ArrayList<Market>();
		
		int filterNumber = 0;
		
		HashMap<String, Object> updatedFields = dataSource.getUpdatedFields();
		for(Map.Entry<String, Object> entry : updatedFields.entrySet())
			updateDocument.add(Updates.set(entry.getKey(), entry.getValue()));
		ListIterator<Market> iterator = dataSource.getMarketsIterator();
		
		while(iterator.hasNext()) {
			Market market = iterator.next();
			switch(market.getState()) 
			{
			case STAGED:
				addedMarkets.add(market);
				market.setState(PojoState.COMMITTED);
				continue;
			case REMOVED:
				removedMarkets.add(market);
				iterator.remove();
				continue;
			default:
			}
			
			updatedFields = market.getUpdatedFields();
			if(updatedFields.isEmpty()) 
				continue;
			String filterName = "f" + filterNumber;
			arrayFilters.add(Filters.eq(filterName + ".id", market.getId()));
			for(Map.Entry<String, Object> entry : updatedFields.entrySet()) {
				updateDocument.add(Updates.set("markets.$["+filterName+"]."+entry.getKey(), entry.getValue()));
			}
			filterNumber++;
		}
		
		if(!addedMarkets.isEmpty())
			//updateDocument.add(Updates.pushEach("markets", addedMarkets));
			getCollection().updateOne(Filters.eq("_id", dataSource.getName()), Updates.pushEach("markets", addedMarkets));
		if(!removedMarkets.isEmpty())
			//updateDocument.add(Updates.pullAll("markets", removedMarkets));			
			getCollection().updateOne(Filters.eq("_id", dataSource.getName()), Updates.pullAll("markets", removedMarkets));
		
		//AGGIUNGERE GESTIONE RIMOZIONE MARKETDATA
		if(!arrayFilters.isEmpty())
			options.arrayFilters(arrayFilters);
		
		dataSource.setState(PojoState.COMMITTED);
		
		if(updateDocument.isEmpty())
			return false;
		
		System.out.println(Updates.combine(updateDocument).toBsonDocument(BsonDocument.class,CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().conventions(Arrays.asList(Conventions.ANNOTATION_CONVENTION, Conventions.SET_PRIVATE_FIELDS_CONVENTION)).automatic(true).build()))).toJson());

		System.out.println(options.toString());
		return getCollection().updateOne(Filters.eq("_id", dataSource.getName()), Updates.combine(updateDocument), options).wasAcknowledged();
	}
	
	public long update(List<DataSource> dataSources) 
	{
		for(DataSource dataSource : dataSources)
			update(dataSource);
		return 0;
	}
	
	//SE SERVE
	//public boolean updateMarket(Market market) {return true;}
	
	
	public void drop() 
	{
		getCollection().drop();
	}
	
	public PojoCursor<DataSource> find(boolean getMarket) //Se false, non si prende i mercati
	{	
		if(!getMarket) {
			FindIterable<DataSource> cursor = getCollection().find().projection(Projections.exclude("markets"));
			return new PojoCursor<DataSource>(cursor.cursor());
		}
		
		FindIterable<DataSource> cursor = getCollection().find();
		return new PojoCursor<DataSource>(cursor.cursor());
	}
	
	public PojoCursor<DataSource> find()
	{
		return find(true);
	}
	
	public Market findMarket() 
	{
		return null;
	}
	//public boolean deleteMarket(Market market) {}
	//public long deleteMarkets(List<Market> markets) {}	
}
