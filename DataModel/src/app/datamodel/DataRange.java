package app.datamodel;

import java.time.Instant;
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
	
	public Instant start;
	public Instant end;
	
	public DataRange() 
	{	
		
	}
	
	public DataRange( Instant start, Instant end) {
;
		this.start = start;
		this.end = end;
	}
}
