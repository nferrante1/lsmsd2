package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;

import app.library.Candle;
import app.library.indicators.enums.InputPrice;

// Exponential Moving Average
public class EMA extends Indicator implements FacetPipeline
{
	private InputPrice inputPrice;
	private int period;
	private int elapsedPeriods;
	private double value = Double.NaN;

	@Override
	public String name()
	{
		return "EMA" + inputPrice.getShortName() + period;
	}

	public EMA(int period, InputPrice inputPrice)
	{
		if(period <= 0)
			throw new IllegalArgumentException("First argument to EMA constructor must be a positive integer (supplied: " + period + ").");
		if (inputPrice == null)
			inputPrice = InputPrice.CLOSE;
		this.period = period;
		this.inputPrice = inputPrice;
	}

	public EMA(int period)
	{
		this(period, InputPrice.CLOSE);
	}

	public EMA(InputPrice inputPrice)
	{
		this(9, inputPrice);
	}

	public EMA()
	{
		this(9);
	}

	@Override
	public List<Bson> pipeline()
	{
		double alpha = 2/((double)period + 1);
		List<Bson> stages = getMappingStages(inputPrice);
		stages.add(Aggregates.addFields(new Field<Document>("candles", new Document("$map",
			new Document("input", new Document("$range", Arrays.asList(0L, new Document("$subtract", Arrays.asList(new Document("$size", "$candles"), 1L)))))
			.append("as", "z")
			.append("in", new Document("value", new Document("$slice", Arrays.asList("$candles.v", new Document("$max", Arrays.asList(0L, new Document("$subtract", Arrays.asList("$$z", period)))), period))))))));
		stages.add(Aggregates.unwind("$candles"));
		stages.add(Aggregates.replaceRoot("$candles"));
		stages.add(Aggregates.addFields(new Field<Document>("value", new Document("$reduce",
			new Document("input", "$value")
			.append("initialValue", new Document("total", new Document("$arrayElemAt", Arrays.asList("$value", 0L))))
			.append("in", new Document("total", new Document("$sum", Arrays.asList(
				new Document("$multiply", Arrays.asList("$$this", alpha)),
				new Document("$multiply", Arrays.asList("$$value.total", 1 - alpha))))))))));
		stages.add(Aggregates.group(new BsonNull(), Accumulators.push("candles", new Document("value", "$value.total"))));
		return stages;
	}

	@Override
	public void compute(Candle candle)
	{
		++elapsedPeriods;
		if(elapsedPeriods > period)
			value = candle.getTa(name());
	}

	public double value()
	{
		return value;
	}

	public int period()
	{
		return period;
	}

	public InputPrice inputPrice()
	{
		return inputPrice;
	}
}
