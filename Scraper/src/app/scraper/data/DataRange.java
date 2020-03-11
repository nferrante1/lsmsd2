package app.scraper.data;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

@CollectionName("MarketData")
public class DataRange  extends Pojo {
	
	@BsonId
	public String id;
	public YearMonth start;
	public YearMonth end;
	
	public DataRange() 
	{	
		
	}
}
