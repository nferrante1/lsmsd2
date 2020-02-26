module server {
	exports app.server.datamodel to com.google.gson;
	opens app.server.datamodel to com.google.gson;
	
	requires org.mongodb.bson;
	requires com.google.gson;
	requires org.mongodb.driver.core;
	requires org.mongodb.driver.sync.client;
}