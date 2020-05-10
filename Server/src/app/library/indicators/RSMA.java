package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import app.library.Candle;

public class RSMA extends Indicator implements ComputableIndicator
{
	private int period;
	private boolean increment;
	private double value;

	public double getValue()
	{
		return this.value;
	}

	public RSMA(int period, boolean increment)
	{
		this.period = period;
		this.increment = increment;
	}

	@Override
	public String getName()
	{
		return "SMA" + (increment ? "u" : "d") + period;
	}

	@Override
	public List<Bson> getPipeline()
	{
		return Arrays.asList(Aggregates.project(Projections.fields(
				Projections.excludeId(), 
				Projections.include("c", "o"))), 
				Aggregates.group(
						new BsonNull(), 
						Accumulators.push("candles", 
						Filters.and(
						Filters.eq("c", "$c"), 
						Filters.eq("o", "$o")))), 
				Aggregates.addFields(new Field<Document>("candles", 
				    new Document("$map", 
				    new Document("input", "$candles")
				                .append("as", "candle")
				                .append("in", 
				                		new Document("v", 
				                				new Document("$max", Arrays.asList(new Document("$subtract", 
						    (increment ? Arrays.asList("$$candle.c", "$$candle.o") : Arrays.asList("$$candle.o", "$$candle.c"))), 0L))))))), 
				Aggregates.addFields(new Field<Document>("candles", 
				    new Document("$map", 
				    new Document("input", 
				    new Document("$range", Arrays.asList(0L, 
				                        new Document("$subtract", Arrays.asList(new Document("$size", "$candles"), 1L)))))
				                .append("as", "z")
				                .append("in", 
				    new Document("value", 
				    new Document("$avg", 
				    new Document("$slice", Arrays.asList("$candles.v", "$$z", period)))))))));
	}

	@Override
	public void compute(Candle candle)
	{
		value = candle.getTa(getName());
	}
}
