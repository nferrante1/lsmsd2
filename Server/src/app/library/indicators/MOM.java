package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;

import app.library.Candle;
import app.library.indicators.enums.InputPrice;

// MOMentum
public class MOM extends Indicator implements FacetPipeline
{
	private InputPrice inputPrice;
	private int period;
	private int elapsedPeriods;
	private double value = Double.NaN;

	public MOM(int period, InputPrice inputPrice)
	{
		if(period <= 0)
			throw new IllegalArgumentException("First argument to MOM constructor must be a positive integer (supplied: " + period + ").");
		if (inputPrice == null)
			inputPrice = InputPrice.CLOSE;
		this.period = period;
		this.inputPrice = inputPrice;
	}

	public MOM(int period)
	{
		this(period, InputPrice.CLOSE);
	}

	public MOM(InputPrice inputPrice)
	{
		this(14, inputPrice);
	}

	public MOM()
	{
		this(14);
	}

	@Override
	public String name()
	{
		return "MOM" + inputPrice.getShortName() + period;
	}

	@Override
	public List<Bson> pipeline()
	{
		List<Bson> stages = getMappingStages(inputPrice);
		stages.add(Aggregates.addFields(new Field<Document>("candles", new Document("$map",
			new Document("input", new Document("$range", Arrays.asList(0L, new Document("$subtract", Arrays.asList(new Document("$size", "$candles"), 1L)))))
			.append("as", "z")
			.append("in", new Document("value", new Document("$subtract", Arrays.asList(
				new Document("$arrayElemAt", Arrays.asList("$candles.v", "$$z")),
				new Document("$arrayElemAt", Arrays.asList("$candles.v", new Document("$subtract", Arrays.asList("$$z", period))))))))))));
		return stages;
	}

	@Override
	public void compute(Candle candle)
	{
		++elapsedPeriods;
		if(elapsedPeriods >= period)
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
