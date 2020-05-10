package app.server.dm;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.datamodel.PojoCursor;
import app.datamodel.PojoManager;
import app.library.Candle;

public class CandleManager extends PojoManager<Candle>
{
	public CandleManager()
	{
		super(Candle.class, "MarketData");
	}

	public PojoCursor<Candle> getCandles(String marketId, int granularity, HashMap<String, List<Bson>> indicators)
	{
		List<Facet> facets = new ArrayList<Facet>();
		facets.add(new Facet("candles", Aggregates.project(Projections.excludeId())));

		Document document = new Document("t", new Document("$arrayElemAt", Arrays.asList("$candles.t", "$$z")))
		.append("o", new Document("$arrayElemAt", Arrays.asList("$candles.o", "$$z")))
		.append("h", new Document("$arrayElemAt", Arrays.asList("$candles.h", "$$z")))
		.append("l", new Document("$arrayElemAt", Arrays.asList("$candles.l", "$$z")))
		.append("c", new Document("$arrayElemAt", Arrays.asList("$candles.c", "$$z")))
		.append("v", new Document("$arrayElemAt", Arrays.asList("$candles.v", "$$z")));

		for(Entry<String, List<Bson>> entry : indicators.entrySet()) {
			facets.add(new Facet(entry.getKey(), entry.getValue()));
			document.append("ta." + entry.getKey(), new Document("$arrayElemAt", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$" + entry.getKey() + ".candles.value", 0)), "$$z")));
		}

		return aggregate(Aggregates.match(Filters.eq("market", marketId)),
				Aggregates.unwind("$candles"),
				Aggregates.replaceRoot("$candles"),
				Aggregates.addFields(new Field<Document>("n",
					new Document("$floor", new Document("$divide", Arrays.asList(new Document("$subtract", Arrays.asList("$t", 
							Instant.EPOCH)), granularity*60*1000))))),
						//new SimpleDateFormat("EEE MMMMM dd yyyy HH:mm:ss").format(new java.util.Date(0L)))), granularity*60*1000))))),
				Aggregates.group("$n",
					Accumulators.first("t", "$t"),
					Accumulators.first("o", "$o"),
					Accumulators.max("h", "$h"),
					Accumulators.min("l", "$l"),
					Accumulators.last("c", "$c"),
					Accumulators.sum("v", "$v")
				),
				Aggregates.sort(Sorts.ascending("t")),
				Aggregates.facet(facets),
				Aggregates.project(Projections.fields(
						new Document("candles",
								new Document("$map",
										new Document("input",
												new Document("$range", Arrays.asList(0,new Document("$subtract",Arrays.asList(new Document("$size", "$candles"), 1))))
														).append("as", "z").append("in", document)
										)
								)
						)
				),
				
				Aggregates.unwind("$candles"),
				Aggregates.replaceRoot("$candles"));
	}
}
