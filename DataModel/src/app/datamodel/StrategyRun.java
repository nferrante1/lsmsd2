package app.datamodel;

import org.bson.types.ObjectId;

import app.datamodel.mongo.NestedDataObject;

public class StrategyRun extends NestedDataObject {
	protected ObjectId id;
	protected User user;
	protected Config config;
	protected Report report;
	
	
}
