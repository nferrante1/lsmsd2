module scraper
{
	requires org.mongodb.driver.sync.client;
	requires org.mongodb.bson;
	requires org.mongodb.driver.core;
	requires com.google.gson;
	requires retrofit2;
	requires retrofit2.converter.gson;
	
	exports app.scraper.data to com.google.gson;
	opens app.scraper.data to com.google.gson;
}