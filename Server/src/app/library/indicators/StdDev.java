package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;

import app.library.Candle;
import app.library.indicators.enums.InputPrice;

// Standard Deviation
public class StdDev extends Indicator implements FacetPipeline
{
	private int period;
	private InputPrice inputPrice;
	private double value = Double.NaN;
	private long elapsedPeriods;

	public StdDev(int period, InputPrice inputPrice)
	{
		if(period <= 0)
			throw new IllegalArgumentException("First argument to StdDev constructor must be a positive integer (supplied: " + period + ").");
		if (inputPrice == null)
			inputPrice = InputPrice.CLOSE;
		this.period = period;
		this.inputPrice = inputPrice;
	}

	public StdDev(int period)
	{
		this(period, InputPrice.CLOSE);
	}

	public StdDev(InputPrice inputPrice)
	{
		this(14, inputPrice);
	}

	public StdDev()
	{
		this(14);
	}

	@Override
	public String name()
	{
		return "StdDev" + inputPrice.getShortName() + period;
	}

	@Override
	public List<Bson> pipeline()
	{
		List<Bson> stages = getMappingStages(inputPrice);
		stages.add(Aggregates.addFields(new Field<Document>("candles", new Document("$map",
			new Document("input", new Document("$range", Arrays.asList(0L, new Document("$subtract", Arrays.asList(new Document("$size", "$candles"), 1L)))))
			.append("as", "z")
			.append("in", new Document("value", new Document("$stdDevPop",
				new Document("$slice", Arrays.asList("$candles.v", new Document("$max", Arrays.asList(0L, new Document("$subtract", Arrays.asList("$$z", period)))), period)))))))));
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
