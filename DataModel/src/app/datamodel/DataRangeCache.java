package app.datamodel;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.datamodel.mongo.DBManager;
import app.datamodel.mongo.EmbeddedPojoManager;
import app.datamodel.mongo.PojoManager;

public class DataRangeCache {
	private class Range {
		public YearMonth start;
		public YearMonth end;
		public Range(YearMonth start, YearMonth end) 
		{
			this.start = start;
			this.end = end;
		}
	}

	HashMap<String, Range> ranges;
	private static DataRangeCache instance;

	private DataRangeCache() 
	{
		ranges = new HashMap<String, Range>();
		
		List<MarketData> marketDatas = MarketData.getManager().aggregate(
				Arrays.asList(
				Aggregates.project(
					Projections.fields(
						Projections.computed(
							"source", new Document(
								"$arrayElemAt", Arrays.asList(
									new Document(
										"$split", Arrays.asList(
											"$_id", ":"
											)
										), 0
									)
								)
							),
						Projections.computed(
							"market", new Document(
								"$arrayElemAt", Arrays.asList(
									new Document(
										"$split", Arrays.asList(
											"$_id", ":"
											)
										), 1
									)
								)
							),
						Projections.computed(
							"month", new Document(
								"$arrayElemAt", Arrays.asList(
									new Document(
										"$split", Arrays.asList(
											"$_id", ":"
											)
										), 2
									)
								)
							),
						Projections.exclude("_id")
						)
					),
				Aggregates.project(Projections.fields(Projections.computed("market", new Document("$concat", Arrays.asList("$source",":" ,"$market"))), Projections.include("month"))),
				Aggregates.sort(Sorts.ascending("month"))));
		if(marketDatas.isEmpty())
			return;
		String id = marketDatas.get(0).getId();
		YearMonth start = marketDatas.get(0).getMonth();
		YearMonth end = marketDatas.get(0).getMonth();
		ranges.put(id, new Range(start, end));
		
	}

	public static synchronized DataRangeCache getInstance()
	{
		if (instance == null)
			instance = new DataRangeCache();
		return instance;
	}
	
	public YearMonth getStartMonth(String marketName) 
	{
		Range range = ranges.get(marketName);
		return range == null ? null : range.start; 
	}
	
	public YearMonth getEndMonth(String marketName) 
	{
		Range range = ranges.get(marketName);
		return range == null ? null : range.end; 
	}
	
	public void setStartMonth(String marketName, YearMonth month) 
	{
		if(ranges.get(marketName) == null)
			ranges.put(marketName, new Range(null, null));
		ranges.get(marketName).start = month;
	}
	
	public void setEndMonth(String marketName, YearMonth month) 
	{
		if(ranges.get(marketName) == null)
			ranges.put(marketName, new Range(null, null));
		ranges.get(marketName).end = month;
			
	}
	
	

}
