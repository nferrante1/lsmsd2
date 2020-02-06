package app.scraper.data.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
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

		mongoClient = MongoClients.create(sb.toString());

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
	
	public void insert(String collectionName, List<String> jsonDocuments)
	{
		List<Document> documents = new ArrayList<Document>();
		for (String jsonDocument: jsonDocuments)
			documents.add(Document.parse(jsonDocument));
		getCollection(collectionName).insertMany(documents);
	}
	
	public void insert(String collectionName, String jsonDocument)
	{
		Document document = Document.parse(jsonDocument);
		getCollection(collectionName).insertOne(document);
	}
	
	public List<String> find(String collectionName, Bson filter)
	{
		List<String> jsonDocuments = new ArrayList<String>();
		MongoCursor<Document> cursor;
		if (filter == null)
			cursor = getCollection(collectionName).find().cursor();
		else
			cursor = getCollection(collectionName).find(filter).cursor();
		try {
			while (cursor.hasNext())
				jsonDocuments.add(cursor.next().toJson());
		} finally {
			cursor.close();
		}
		return jsonDocuments;
	}
	
	public List<String> find(String collectionName)
	{
		return find(collectionName, null);
	}
	
	public long update(String collectionName, Bson filter, Bson update)
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
}
