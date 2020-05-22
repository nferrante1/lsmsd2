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

public class MOM extends Indicator implements ComputableIndicator
{
	private int period;
	private int elapsedPeriods;
	private double value = Double.NaN;

	public MOM(int period)
	{
		this.period = period;
	}

	public MOM()
	{
		this(14);
	}

	@Override
	public String getName()
	{
		return "MOM" + period;
	}

	@Override
	public List<Bson> getPipeline()
	{
		List<Bson> stages = new ArrayList<Bson>();
		stages.add(Aggregates.project(Projections.fields(Projections.excludeId(), Projections.include("c", "$c"))));
		stages.add(Aggregates.group(new BsonNull(),Accumulators.push("candles", new Document("c", "$c"))));
		stages.add(Aggregates.addFields(new Field<Document>("candles",
				new Document("$map",
					new Document("input", new Document("$range", Arrays.asList(0L, new Document("$subtract", Arrays.asList(new Document("$size", "$candles"), 1L)))))
				.append("as", "z")
				.append("in",
					new Document("value", new Document("$subtract",
							Arrays.asList(
									new Document("$arrayElemAt",
											Arrays.asList("$candles.c", "$$z")
											), new Document("$arrayElemAt",
													Arrays.asList("$candles.c",
															new Document("$subtract", Arrays.asList("$candles.c", period)
																	)
															)
													)
									)
							)
						)
					)
				))));
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
