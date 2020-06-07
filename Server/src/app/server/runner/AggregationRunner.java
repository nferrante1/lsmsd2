package app.server.runner;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.datamodel.PojoCursor;
import app.datamodel.PojoManager;
import app.datamodel.pojos.DataRange;
import app.library.Candle;

public final class AggregationRunner extends PojoManager<Candle>
{
	private final String marketId;
	private final boolean inverseCross;
	private final int granularity;
	private final Instant start;
	private final Instant end;

	public AggregationRunner(String marketId, boolean inverseCross, int granularity, DataRange range)
	{
		super(Candle.class, "MarketData");
		this.marketId = marketId;
		this.inverseCross = inverseCross;
		this.granularity = granularity;
		this.start = range.start;
		this.end = range.end;
	}

	@Override
	protected AggregateIterable<Candle> getAggregateIterable(List<Bson> pipeline)
	{
		return getCollection().aggregate(pipeline).allowDiskUse(true);
	}

	public PojoCursor<Candle> runAggregation(HashMap<String, List<Bson>> taFacets)
	{
		List<Facet> facets = new ArrayList<Facet>();
		facets.add(new Facet("candles", Aggregates.project(Projections.excludeId())));

		Document document = new Document("t", new Document("$arrayElemAt", Arrays.asList("$candles.t", "$$z")))
			.append("o", new Document("$arrayElemAt", Arrays.asList("$candles.o", "$$z")))
			.append("h", new Document("$arrayElemAt", Arrays.asList("$candles.h", "$$z")))
			.append("l", new Document("$arrayElemAt", Arrays.asList("$candles.l", "$$z")))
			.append("c", new Document("$arrayElemAt", Arrays.asList("$candles.c", "$$z")))
			.append("v", new Document("$arrayElemAt", Arrays.asList("$candles.v", "$$z")));

		Document taDoc = new Document();
		for(Entry<String, List<Bson>> entry: taFacets.entrySet()) {
			String name = entry.getKey();
			List<Bson> pipeline = entry.getValue();
			if (pipeline == null || pipeline.isEmpty())
				continue;
			facets.add(new Facet(name, pipeline));
			taDoc.append(name, new Document("$arrayElemAt", Arrays.asList(new Document("$arrayElemAt", Arrays.asList("$" + name + ".candles.value", 0)), "$$z")));
		}
		if (!taDoc.isEmpty())
			document.append("ta", taDoc);

		List<Bson> stages = new ArrayList<Bson>();
		stages.add(Aggregates.match(Filters.eq("market", marketId)));
		stages.add(Aggregates.unwind("$candles"));
		stages.add(Aggregates.replaceRoot("$candles"));
		stages.add(Aggregates.match(Filters.and(Filters.gte("t", start), Filters.lte("t", end))));

		List<Field<Document>> fields = new ArrayList<Field<Document>>();
		fields.add(new Field<Document>("n", new Document("$floor", new Document("$divide", Arrays.asList(new Document("$subtract", Arrays.asList("$t", Instant.EPOCH)), granularity*60*1000)))));
		if (inverseCross) {
			fields.add(new Field<Document>("o", new Document("$divide", Arrays.asList(1, "$o"))));
			fields.add(new Field<Document>("h", new Document("$divide", Arrays.asList(1, "$h"))));
			fields.add(new Field<Document>("l", new Document("$divide", Arrays.asList(1, "$l"))));
			fields.add(new Field<Document>("c", new Document("$divide", Arrays.asList(1, "$c"))));
		}

		stages.add(Aggregates.addFields(fields.toArray(new Field[0])));
		stages.add(Aggregates.group("$n",
			Accumulators.first("t", "$t"),
			Accumulators.first("o", "$o"),
			Accumulators.max("h", "$h"),
			Accumulators.min("l", "$l"),
			Accumulators.last("c", "$c"),
			Accumulators.sum("v", "$v")));
		stages.add(Aggregates.sort(Sorts.ascending("t")));
		stages.add(Aggregates.facet(facets));
		stages.add(Aggregates.project(Projections.fields(new Document("candles", new Document("$map",
			new Document("input", new Document("$range", Arrays.asList(0, new Document("$subtract", Arrays.asList(new Document("$size", "$candles"), 1)))))
			.append("as", "z")
			.append("in", document))))));
		stages.add(Aggregates.unwind("$candles"));
		stages.add(Aggregates.replaceRoot("$candles"));
//		for(Bson stage : stages)
//			System.out.println(stage.toString());
//		return null;
		return aggregate(stages);
	}
}
