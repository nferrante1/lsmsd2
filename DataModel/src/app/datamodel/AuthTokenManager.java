package app.datamodel;

public class AuthTokenManager {
/*
	protected static MongoDatabase getDB()
	{
		return DBManager.getInstance().getDatabase();
	}

	protected MongoCollection<AuthToken> getCollection()
	{
		return getDB().getCollection("AuthToken", AuthToken.class);
	}
	// done
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
	 //done
	public void insert(AuthToken token) 
	{
		getCollection().insertOne(token);
		token.setState(StorablePojoState.COMMITTED);
	}
	//done
	public void insert(List<AuthToken> tokens)
	{
		getCollection().insertMany(tokens);
		for(AuthToken t : tokens) {
			t.setState(StorablePojoState.COMMITTED);
		}
	}
	//done
	public boolean delete(AuthToken token)
	{
		token.setState(StorablePojoState.REMOVED);
		return getCollection().deleteOne(Filters.eq("_id", token.getId())).wasAcknowledged();		
	}
	//done
	public long delete(List<AuthToken> tokens) 
	{
		long result = 0;
		for(AuthToken token : tokens)
			if(delete(token))
				result++;
		return result;
	}
	//done
	public boolean update(AuthToken token) 
	{
		List<Bson> updateDocument = new ArrayList<Bson>();

		HashMap<String, Object> updatedFields = token.getUpdatedFields();
		for(Map.Entry<String, Object> entry : updatedFields.entrySet())
			updateDocument.add(Updates.set(entry.getKey(), entry.getValue()));
		
		token.setState(StorablePojoState.COMMITTED);
		
		if(updateDocument.isEmpty())
			return false;
		
		//STAMPA
		System.out.println(Updates.combine(updateDocument).toBsonDocument(BsonDocument.class,CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().conventions(Arrays.asList(Conventions.ANNOTATION_CONVENTION, Conventions.SET_PRIVATE_FIELDS_CONVENTION)).automatic(true).build()))).toJson());

		return getCollection().updateOne(Filters.eq("_id", token.getId()), Updates.combine(updateDocument)).wasAcknowledged();

	}
	//done
	public long update(List<AuthToken> tokens) 
	{
		long result = 0;
		for(AuthToken token : tokens)
			if(update(token))
				result++;
		return result;
	}
	//done find + generateFilter
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
	//done find
	public AuthToken find(String id) 
	{
		return find("_id", id).first();
	}
	
	//done
	public void drop() 
	{
		getCollection().drop();
	}
	*/
}
