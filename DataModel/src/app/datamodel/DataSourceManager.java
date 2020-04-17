package app.datamodel;

import app.datamodel.pojos.DataSource;

public class DataSourceManager extends StorablePojoManager<DataSource>
{
	public DataSourceManager()
	{
		super(DataSource.class);
	}
/*
	// done
	public void update(DataSource dataSource) 
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
				market.setState(StorablePojoState.COMMITTED);
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
		
		dataSource.setState(StorablePojoState.COMMITTED);
		
		if(updateDocument.isEmpty())
			return;
		
		System.out.println(Updates.combine(updateDocument).toBsonDocument(BsonDocument.class,CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().conventions(Arrays.asList(Conventions.ANNOTATION_CONVENTION, Conventions.SET_PRIVATE_FIELDS_CONVENTION)).automatic(true).build()))).toJson());

		System.out.println(options.toString());
		getCollection().updateOne(Filters.eq("_id", dataSource.getName()), Updates.combine(updateDocument), options).wasAcknowledged();
	}
	// FindExclude
	public StorablePojoCursor<DataSource> findWithoutMarkets()
	{	
		return super.find(null, Projections.exclude("markets"));
	}
	
	/* RUN AGGREGATE
	public List<Market> findMarketName(String marketName, int limit, int skip, boolean admin) 
	{
		//CAMBIARE PIPELINE
		List<Market> markets = new ArrayList<Market>();
		
		List<Bson> projections = Arrays.asList(Projections.excludeId(), Projections.computed("value", Filters.eq("$concat", Arrays.asList("$_id", ":", "$markets.id"))), Projections.computed("granularity", "$markets.granularity"));
		
		if(admin)
			projections.addAll(Arrays.asList(Projections.computed("sync", "$markets.sync"), Projections.computed("selectable", "$markets.selectable")));
		List<Bson> stages = Arrays.asList(Aggregates.unwind("$markets"), Aggregates.match(Filters.regex("markets.id", marketName)), Aggregates.project(Projections.fields(projections)));
		if(skip != 0)
			stages.add(Aggregates.skip(skip));
		if(limit != 0)
			stages.add(Aggregates.limit(limit));
		AggregateIterable<Market> aggregates =  getMarketCollection().aggregate(stages);
		MongoCursor<Market> cursor = aggregates.cursor();
		while(cursor.hasNext())
			markets.add(cursor.next());
		return markets;		
	}*/
	
	//public boolean deleteMarket(Market market) {}
	//public long deleteMarkets(List<Market> markets) {}
}
