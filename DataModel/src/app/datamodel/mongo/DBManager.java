package app.datamodel.mongo;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public final class DBManager implements Closeable
{
	private static DBManager instance;

	private static String hostname;
	private static int port;
	private static String username;
	private static String password;
	private static String databaseName;
	private static List<Codec<?>> codecs;
	private static HashMap<String, String> options;

	private MongoClient mongoClient;
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

		if (options != null && !options.isEmpty()) {
			sb.append("/?");
			for (Map.Entry<String, String> option: options.entrySet())
				sb.append(option.getKey() + "=" + option.getValue() + "&");
			sb.deleteCharAt(sb.length() - 1);
		}
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
						CodecRegistries.fromProviders(PojoCodecProvider.builder().conventions(Arrays.asList(Conventions.ANNOTATION_CONVENTION, Conventions.SET_PRIVATE_FIELDS_CONVENTION)).automatic(true).build()));
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(sb.toString())).codecRegistry(CodecRegistries.fromRegistries(/*CodecRegistries.fromCodecs(codecs),*/pojoCodecRegistry)).build();
		mongoClient = MongoClients.create(settings);

		if (databaseName == null || databaseName.isEmpty())
			databaseName = "admin";
	}

	public static synchronized void setHostname(String hostname)
	{
		DBManager.hostname = hostname;
	}

	public static synchronized void setPort(int port)
	{
		DBManager.port = port;
	}

	public static synchronized void setUsername(String username)
	{
		DBManager.username = username;
	}

	public static synchronized void setPassword(String password)
	{
		DBManager.password = password;
	}

	public static synchronized void setDatabase(String databaseName)
	{
		DBManager.databaseName = databaseName;
	}

	public static synchronized void setOption(String key, String value)
	{
		if (options == null)
			options = new HashMap<String, String>();
		options.put(key, value);
	}

	public static synchronized void setOption(String key, int value)
	{
		setOption(key, Integer.toString(value));
	}

	public static synchronized void setOption(String key, boolean value)
	{
		setOption(key, Boolean.toString(value));
	}

	public static synchronized <E extends Enum<E>> void setOption(String key, E value)
	{
		setOption(key, value.toString());
	}

	public static synchronized void addCodec(Codec<?> codec)
	{
		if (codecs == null)
			codecs = new ArrayList<Codec<?>>();
		codecs.add(codec);
	}

	public static synchronized DBManager getInstance()
	{
		if (instance == null)
			instance = new DBManager();
		return instance;
	}

	synchronized public MongoDatabase getDatabase()
	{
		if (instance == null)
			throw new IllegalStateException("Called getDatabase() on a uninitialized instance.");
		if (mongoDatabase == null) {
			mongoDatabase = mongoClient.getDatabase(databaseName);
			init();
		}
		return mongoDatabase;
	}
	
	private void init()
	{
		IndexOptions indexOptions = new IndexOptions()
			.name("authTokenTTLIndex")
			.expireAfter(0L, TimeUnit.SECONDS);
		Bson keys = new Document("expireTime", 1);
		mongoDatabase.getCollection("AuthTokens").createIndex(keys, indexOptions);

		indexOptions = new IndexOptions()
			.name("marketIdIndex")
			.unique(true);
		keys = new Document("_id", 1).append("markets.id", 1);
		mongoDatabase.getCollection("Sources").createIndex(keys, indexOptions);

		indexOptions = new IndexOptions().name("marketHashed");
		keys = new Document("market", "hashed");
		mongoDatabase.getCollection("MarketData").createIndex(keys, indexOptions);

		indexOptions = new IndexOptions().name("startIndex");
		keys = new Document("start", 1);
		mongoDatabase.getCollection("MarketData").createIndex(keys, indexOptions);

		indexOptions = new IndexOptions()
			.name("nameIndex")
			.unique(true);
		keys = new Document("name", 1);
		mongoDatabase.getCollection("Strategies").createIndex(keys, indexOptions);

		indexOptions = new IndexOptions()
			.name("runIdIndex")
			.unique(true);
		keys = new Document("runs.id", 1);
		mongoDatabase.getCollection("Strategies").createIndex(keys, indexOptions);

		indexOptions = new IndexOptions().name("reportNetProfitIndex");
		keys = new Document("runs.report.netProfit", 1);
		mongoDatabase.getCollection("Strategies").createIndex(keys, indexOptions);
	}

	@Override
	public synchronized void close()
	{
		if (instance == null)
			throw new IllegalStateException("Called close() on a uninitialized instance.");
		if (mongoClient != null) {
			mongoClient.close();
			mongoClient = null;
		}
		instance = null;
	}
}
