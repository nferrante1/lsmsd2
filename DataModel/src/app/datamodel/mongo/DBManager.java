package app.datamodel.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public final class DBManager
{
	private static DBManager instance;
	
	private static String hostname;
	private static int port;
	private static String username;
	private static String password;
	private static String databaseName;
	private static List<Codec<?>> codecs;
	private static HashMap<String, String> options;
	
	private final MongoClient mongoClient;
	private MongoDatabase mongoDatabase;
	
	private DBManager()
	{
		if (hostname == null || hostname.isEmpty())
			hostname = "localhost";

		StringBuilder sb = new StringBuilder("mongodb://");

		if (username != null && !username.isEmpty()) {
			sb.append(username);
			if (password != null && !password.isEmpty())
				sb.append(":" + password + "@");
		}

		sb.append(hostname);
		if (port > 0 && port < 65536)
			sb.append(":" + port);

		boolean customDbName = databaseName != null && !databaseName.isEmpty();
		//if (customDbName)
		//	sb.append("/" + databaseName);

		if (options != null && !options.isEmpty()) {
		//	if (!customDbName)
				sb.append("/");
			sb.append("?");
			for (Map.Entry<String, String> option: options.entrySet())
				sb.append(option.getKey() + "=" + option.getValue() + "&");
			sb.deleteCharAt(sb.length() - 1);
		}
		
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
		                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(sb.toString())).codecRegistry(CodecRegistries.fromRegistries(/*CodecRegistries.fromCodecs(codecs),*/pojoCodecRegistry)).build();
		mongoClient = MongoClients.create(settings);

		if (!customDbName)
			databaseName = "admin";
	}
	
	public static void setHostname(String hostname)
	{
		DBManager.hostname = hostname;
	}
	
	public static void setPort(int port)
	{
		DBManager.port = port;
	}
	
	public static void setUsername(String username)
	{
		DBManager.username = username;
	}
	
	public static void setPassword(String password)
	{
		DBManager.password = password;
	}
	
	public static void setDatabase(String databaseName)
	{
		DBManager.databaseName = databaseName;
	}
	
	public static void setOption(String key, String value)
	{
		if (options == null)
			options = new HashMap<String, String>();
		options.put(key, value);
	}
	
	public static void setOption(String key, int value)
	{
		setOption(key, Integer.toString(value));
	}
	
	public static void setOption(String key, boolean value)
	{
		setOption(key, Boolean.toString(value));
	}
	
	public static <E extends Enum<E>> void setOption(String key, E value)
	{
		setOption(key, value.toString());
	}
	
	public static void addCodec(Codec<?> codec)
	{
		if (codecs == null)
			codecs = new ArrayList<Codec<?>>();
		codecs.add(codec);
	}
	
	public static DBManager getInstance()
	{
		if (instance == null)
			instance = new DBManager();
		return instance;
	}
	
	private MongoDatabase getDatabase()
	{
		if (mongoDatabase == null)
			mongoDatabase = mongoClient.getDatabase(databaseName);
		return mongoDatabase;
	}
	
	private MongoCollection<Document> getCollection(String collectionName)
	{
		return getDatabase().getCollection(collectionName);
	}
	
	public void insert(String collectionName, List<Document> documents)
	{
		getCollection(collectionName).insertMany(documents);
	}
	
	public void insert(String collectionName, Document document)
	{
		getCollection(collectionName).insertOne(document);
	}
	
	public List<Document> find(String collectionName, Bson filter, String sortField, boolean ascending, int skip, int limit)
	{
		List<Document> documents = new ArrayList<Document>();
		MongoCursor<Document> cursor;
		FindIterable<Document> result;
		if (filter == null)
			result = getCollection(collectionName).find();
		else
			result = getCollection(collectionName).find(filter);
		
		if(skip > 0) 
			result = result.skip(skip);
		if(limit > 0) 
			result = result.limit(limit);
		if(sortField != null)
			if(ascending)
				result = result.sort(Sorts.ascending(sortField));
			else
				result = result.sort(Sorts.descending(sortField));
		
		cursor = result.cursor();
		
		try {
			while (cursor.hasNext())
				documents.add(cursor.next());
		} finally {
			cursor.close();
		}
		return documents;
	}
	
	public List<Document> find(String collectionName, Bson filter, String sortField, boolean ascending){
		return find(collectionName, filter, sortField, ascending, 0, 0);
	}
	public List<Document> find(String collectionName, Bson filter, String sortField){
		return find(collectionName, filter, sortField, true);
	}
	
	public List<Document> find(String collectionName, Bson filter)
	{
		return find(collectionName, filter, null);
	}
	
	public List<Document> find(String collectionName)
	{
		return find(collectionName, null);
	}
	
	public long update(String collectionName, Bson filter, Document update)
	{
		UpdateResult updateResult = getCollection(collectionName).updateMany(filter, update);
		return updateResult.getModifiedCount();
	}
	
	public long delete(String collectionName, Bson filter)
	{
		DeleteResult deleteResult = getCollection(collectionName).deleteMany(filter);
		return deleteResult.getDeletedCount();
	}
	
	public boolean updateOne(String collectionName, Bson filter, Bson update)
	{
		UpdateResult updateResult = getCollection(collectionName).updateOne(filter, update);
		return updateResult.wasAcknowledged();
	}
	
	public boolean deleteOne(String collectionName, Bson filter)
	{
		DeleteResult deleteResult = getCollection(collectionName).deleteOne(filter);
		return deleteResult.wasAcknowledged();
	}
	
	public boolean replaceOne(String collectionName, Bson filter, Document replacement)
	{
		UpdateResult updateResult = getCollection(collectionName).replaceOne(filter, replacement);
		return updateResult.wasAcknowledged();
	}
	public List<Document> aggregate(String collectionName, List<Bson> stages)
	{
		List<Document> documents = new ArrayList<Document>();
		AggregateIterable<Document> aggregateResult = getCollection(collectionName).aggregate(stages);
		for(Document document : aggregateResult) {
			documents.add(document);
		}
		return documents;
	}
	
	public List<Document> findEmbedded(String collectionName, Bson filter, String subField, String sortField, boolean ascending, int skip, int limit)
	{
		List<Bson> stages = new ArrayList<Bson>();
		stages.add(Aggregates.match(filter));
		stages.add(Aggregates.unwind("$" + subField));
		stages.add(Aggregates.replaceRoot("$markets"));
		if (subField != null)
			stages.add(Aggregates.sort(ascending ? Sorts.ascending(sortField) : Sorts.descending(sortField)));
		if (skip > 0)
			stages.add(Aggregates.skip(skip));
		if (limit > 0)
			stages.add(Aggregates.limit(limit));
		return aggregate(collectionName, stages);
	}
	
	public List<Document> findEmbedded(String collectionName, Bson filter, String subField)
	{
		return findEmbedded(collectionName, filter, subField, null);
	}

	public List<Document> findEmbedded(String collectionName, Bson filter, String subField, String sortField)
	{
		return findEmbedded(collectionName, filter, subField, sortField, true);
	}

	public List<Document> findEmbedded(String collectionName, Bson filter, String subField, String sortField, boolean ascending)
	{
		return findEmbedded(collectionName, filter, subField, sortField, ascending, 0, 0);
	}

	public List<Document> findEmbedded(String collectionName, Bson filter, String subField, int skip, int limit)
	{
		return findEmbedded(collectionName, filter, subField, null, false, skip, limit);
	}
}
