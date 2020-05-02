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
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public final class DBManager implements Closeable
{
	private static DBManager instance;
	
	private static String connectionString = "mongodb://localhost:27017,localhost:27018";
	private static String databaseName = "mydb";
	private static List<Codec<?>> codecs;

	private MongoClient mongoClient;
	private MongoDatabase mongoDatabase;
	
	private static ReadConcern readConcern = ReadConcern.LOCAL;
	private static WriteConcern writeConcern = WriteConcern.MAJORITY;
	private static ReadPreference readPreference = ReadPreference.primary();
	
	private DBManager()
	{
		
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
						CodecRegistries.fromProviders(PojoCodecProvider.builder().conventions(Arrays.asList(Conventions.ANNOTATION_CONVENTION, Conventions.SET_PRIVATE_FIELDS_CONVENTION)).automatic(true).build()));
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(connectionString)).codecRegistry(CodecRegistries.fromRegistries(pojoCodecRegistry)).build();
		mongoClient = MongoClients.create(settings);
	}
	
	public static synchronized void setDatabaseName(String name) {
		databaseName = name;
	}
	
	public static synchronized void setConnectionString(String conn) {
		connectionString = conn;
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
			mongoDatabase = mongoClient.getDatabase(databaseName).
					withReadPreference(getReadPreference()).
					withReadConcern(getReadConcern()).
					withWriteConcern(getWriteConcern());
			
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

	public static ReadConcern getReadConcern()
	{
		return readConcern;
	}

	public static void setReadConcern(ReadConcern readConcern)
	{
		DBManager.readConcern = readConcern;
	}

	public static WriteConcern getWriteConcern()
	{
		return writeConcern;
	}

	public static void setWriteConcern(WriteConcern writeConcern)
	{
		DBManager.writeConcern = writeConcern;
	}

	public static ReadPreference getReadPreference()
	{
		return readPreference;
	}

	public static void setReadPreference(ReadPreference readPreference)
	{
		DBManager.readPreference = readPreference;
	}
}
