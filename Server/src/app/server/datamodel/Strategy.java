package app.server.datamodel;

import app.server.datamodel.mongo.DataObject;

public class Strategy extends DataObject {
	protected String id;
	protected String name;
	protected User user;
	protected StrategyRun runs;
}
