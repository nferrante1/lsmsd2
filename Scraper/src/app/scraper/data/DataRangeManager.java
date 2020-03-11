package app.scraper.data;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.datamodel.MarketData;
import app.datamodel.mongo.PojoManager;


public class DataRangeManager extends PojoManager<DataRange>{

	public DataRangeManager()
	{
		super(DataRange.class);
	}

	
	public List<DataRange> getRanges()
	{
		List<DataRange> ranges = aggregate(
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
						Projections.excludeId()
						)
					),
				Aggregates.project(Projections.fields(Projections.computed("market", new Document("$concat", Arrays.asList("$source",":","$market"))), Projections.include("month"))),
				Aggregates.sort(Sorts.ascending("month")),
				Aggregates.group("$market",  Arrays.asList(Accumulators.first("start","$$ROOT"),Accumulators.last("end", "$$ROOT"))),
				Aggregates.project(Projections.fields(Projections.exclude(Arrays.asList("start.month", "end.month"))))));
		if(ranges.isEmpty())
			return null;
		return ranges;
	}
}
