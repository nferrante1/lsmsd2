package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Projections;

import app.library.Candle;

public class EMA extends Indicator implements ComputableIndicator
{
	private int period;
	private int elapsedPeriods;
	private double value = Double.NaN;

	@Override
	public String getName()
	{
		return "EMA" + period;
	}

	public EMA(int period)
	{
		if(period <= 0) throw new IllegalArgumentException("Period must be a positive integer");
		this.period = period;
	}

	public EMA()
	{
		this(9);
	}

	@Override
	public List<Bson> getPipeline()
	{
		double alpha = 2/((double)period + 1);
		return Arrays.asList(
				Aggregates.project(Projections.fields(Projections.include("c"), Projections.excludeId())),
				Aggregates.group(new BsonNull(), Accumulators.push("candles", "$c")),
				Aggregates.addFields(new Field<Document>("candles",
				new Document("$map",
				new Document("input",
				new Document("$range", Arrays.asList(0L,
				new Document("$subtract", Arrays.asList(new Document("$size", "$candles"), 1L)))))
				.append("as", "z")
				.append("in",
				new Document("value",
				new Document("$slice", Arrays.asList("$candles",
				new Document("$max", Arrays.asList(0L,
				new Document("$subtract", Arrays.asList("$$z", period)))), period))))))),
				Aggregates.unwind("$candles"),
				Aggregates.replaceRoot("$candles"),
				Aggregates.addFields(new Field<Document>("value",
				new Document("$reduce",
				new Document("input", "$value")
				.append("initialValue",
				new Document("total",
				new Document("$arrayElemAt", Arrays.asList("$value", 0L))))
				.append("in",
				new Document("total",
				new Document("$sum", Arrays.asList(new Document("$multiply", Arrays.asList("$$this", alpha)),
					new Document("$multiply", Arrays.asList("$$value.total", (1-alpha)))))))))),
				Aggregates.project(Projections.computed("candle", new Document("value", "$value.total"))),
				Aggregates.group(new BsonNull(), Accumulators.push("candles", "$candle")));
	}

	@Override
	public void compute(Candle candle)
	{
		++elapsedPeriods;
		if(elapsedPeriods > period)
			value = candle.getTa(getName());
	}

	public double getValue()
	{
		return value;
	}

	public int getPeriod()
	{
		return period;
	}
}
