package app.library.indicators;

import java.util.ArrayList;
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
import app.library.indicators.enums.InputPrice;

public class SMA extends Indicator implements ComputableIndicator {

	private int period;
	private InputPrice inputPrice;
	private double value = Double.NaN;
	private long elapsedPeriods;
	public SMA(int period, InputPrice input) {
		if(period <= 0)
			throw new IllegalArgumentException("Period must be a positive number");
		this.period = period;
		if(input == null)
			throw new IllegalArgumentException("Input price must not be null");
		this.inputPrice = input;		
	}
	
	public SMA(int period) {
		this(period, InputPrice.CLOSE);
	}
	
	@Override
	public String getName()
	{
		return "SMA" + inputPrice.getShortName() + period;
	}

	@Override
	public List<Bson> getPipeline()
	{
		List<Bson> projections = new ArrayList<Bson>();
		Document push = new Document("c", "$c");
		Document mapDoc = null;
		projections.add(Projections.excludeId());
	
		switch(inputPrice) {
			case CLOSE:
				projections.add(Projections.computed("v" , "$c"));
				break;
			case INCREMENT:
				projections.add(Projections.include("c","o"));
				push.append("o", "$o");
				mapDoc = new Document("$max", Arrays.asList(
						new Document("$subtract",
								Arrays.asList("$$candle.c", "$$candle.o")), 0L));
				break;
			case DECREMENT:
				projections.add(Projections.include("c","o"));
				push.append("o", "$o");
				mapDoc = new Document("$max", Arrays.asList(
						new Document("$subtract",
								Arrays.asList("$$candle.o", "$$candle.c")), 0L));
				break;
			case TRUE_RANGE:
				projections.add(Projections.include("h","l","c"));
				push.append("l", "$l");
				push.append("h", "$h");
				mapDoc = new Document("$max", Arrays.asList(new Document("$subtract", 
						Arrays.asList("$h","$l")), 
						new Document("$abs", 
							new Document("$subtract",
									Arrays.asList("$h", "$c")
								)
							)
						, new Document("$abs", 
							new Document("$subtract", 
								Arrays.asList("$l", "$c")
								)
							)
						)
					);
				break;
			case TYPICAL:
				projections.add(Projections.include("h","l","c"));
				push.append("l", "$l");
				push.append("h", "$h");
				mapDoc = new Document("$divide", Arrays.asList(new Document("$sum", Arrays.asList("$h","$l","$c")),3));
				break;			
		}
		
		List<Bson> stages = new ArrayList<Bson>();
		stages.add(Aggregates.project(Projections.fields(projections)));
		stages.add(Aggregates.group(new BsonNull(),Accumulators.push("candles", push)));
		if(inputPrice != InputPrice.CLOSE)
			stages.add(Aggregates.addFields(new Field<Document>("candles", new Document("$map",
					new Document("input", "$candles")
					.append("as", "candle")
					.append("in", new Document("v", mapDoc))))));
		stages.add(Aggregates.addFields(new Field<Document>("candles",						
				new Document("$map",
					new Document("input", new Document("$range", Arrays.asList(0L, new Document("$subtract", Arrays.asList(new Document("$size", "$candles"), 1L)))))
				.append("as", "z")
				.append("in",
					new Document("value", new Document("$avg", new Document("$slice", Arrays.asList("$candles.v", "$$z", period)))))))));
		return stages;
	}

	@Override
	public void compute(Candle candle)
	{	
		++elapsedPeriods;
		if(elapsedPeriods >= period)
			value = candle.getTa(getName());
	}

	public double getValue()
	{
		return value;
	}

}
