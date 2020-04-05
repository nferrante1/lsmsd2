package app.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.AuthToken;
import app.datamodel.pojos.PojoState;
import app.datamodel.pojos.AuthToken;

public class AuthTokenManager {

	protected static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}

	protected MongoCollection<AuthToken> getCollection()
	{
		return getDB().getCollection("AuthToken", AuthToken.class);
	}
	
	public void save(AuthToken token)
	{
		switch(token.getState())
		{
		case STAGED:
			insert(token);
			return;
		case COMMITTED:
			update(token);
			return;
		case REMOVED:
			delete(token);
			return;
		default:
		}
	}
	
	public void insert(AuthToken token) 
	{
		getCollection().insertOne(token);
		token.setState(PojoState.COMMITTED);
	}
	
	public void insert(List<AuthToken> tokens)
	{
		getCollection().insertMany(tokens);
		for(AuthToken t : tokens) {
			t.setState(PojoState.COMMITTED);
		}
	}
	
	public boolean delete(AuthToken token)
	{
		token.setState(PojoState.REMOVED);
		return getCollection().deleteOne(Filters.eq("_id", token.getId())).wasAcknowledged();		
	}
	
	public long delete(List<AuthToken> tokens) 
	{
		long result = 0;
		for(AuthToken token : tokens)
			if(delete(token))
				result++;
		return result;
	}
	
	public boolean update(AuthToken token) 
	{
		List<Bson> updateDocument = new ArrayList<Bson>();

		HashMap<String, Object> updatedFields = token.getUpdatedFields();
		for(Map.Entry<String, Object> entry : updatedFields.entrySet())
			updateDocument.add(Updates.set(entry.getKey(), entry.getValue()));
		
		token.setState(PojoState.COMMITTED);
		
		if(updateDocument.isEmpty())
			return false;
		
		//STAMPA
		System.out.println(Updates.combine(updateDocument).toBsonDocument(BsonDocument.class,CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().conventions(Arrays.asList(Conventions.ANNOTATION_CONVENTION, Conventions.SET_PRIVATE_FIELDS_CONVENTION)).automatic(true).build()))).toJson());

		return getCollection().updateOne(Filters.eq("_id", token.getId()), Updates.combine(updateDocument)).wasAcknowledged();

	}

	public long update(List<AuthToken> tokens) 
	{
		long result = 0;
		for(AuthToken token : tokens)
			if(update(token))
				result++;
		return result;
	}
	
	public PojoCursor<AuthToken> find(String fieldName, String value)
	{
		FindIterable<AuthToken> cursor;
		if(value.isEmpty()) return null;
		
		if(fieldName.isEmpty())
			cursor = getCollection().find(Filters.eq("_id", value));
		else 
		{
			cursor = getCollection().find(Filters.eq(fieldName, value));
		}
			
		return new PojoCursor<AuthToken>(cursor.cursor());
	}
	
	public AuthToken find(String id) 
	{
		return find("_id", id).first();
	}
	
	
	public void drop() 
	{
		getCollection().drop();
	}
}
