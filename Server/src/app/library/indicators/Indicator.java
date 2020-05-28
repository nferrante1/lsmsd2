package app.library.indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Projections;

import app.library.Candle;
import app.library.indicators.enums.InputPrice;

public abstract class Indicator
{
	public List<Indicator> depends()
	{
		return new ArrayList<Indicator>();
	}

	abstract public void compute(Candle candle);

	private static Bson getProjection(InputPrice inputPrice)
	{
		List<Bson> projections = new ArrayList<Bson>();
		projections.add(Projections.excludeId());
		switch (inputPrice) {
		case OPEN:
			projections.add(Projections.include("o"));
			break;
		case HIGH:
			projections.add(Projections.include("h"));
			break;
		case LOW:
			projections.add(Projections.include("l"));
			break;
		case CLOSE:
			projections.add(Projections.include("c"));
			break;
		case VOLUME:
			projections.add(Projections.include("v"));
			break;
		case INCREMENT:
		case DECREMENT:
			projections.add(Projections.include("c", "o"));
			break;
		case TRUE_RANGE:
		case TYPICAL:
			projections.add(Projections.include("h", "l", "c"));
			break;
		default:
			throw new IllegalArgumentException("Invalid input price.");
		}
		return Projections.fields(projections);
	}

	private static BsonField getAccumulator(InputPrice inputPrice)
	{
		Document pushDoc;
		switch (inputPrice) {
		case OPEN:
			pushDoc = new Document("v", "$o");
			break;
		case HIGH:
			pushDoc = new Document("v", "$h");
			break;
		case LOW:
			pushDoc = new Document("v", "$l");
			break;
		case CLOSE:
			pushDoc = new Document("v", "$c");
			break;
		case VOLUME:
			pushDoc = new Document("v", "$v");
			break;
		case INCREMENT:
		case DECREMENT:
			pushDoc = new Document("c", "$c").append("o", "$o");
			break;
		case TRUE_RANGE:
		case TYPICAL:
			pushDoc = new Document("c", "$c").append("l", "$l").append("h", "$h");
		default:
			throw new IllegalArgumentException("Invalid input price.");
		}
		return Accumulators.push("candles", pushDoc);
	}

	private static Document getMapDocument(InputPrice inputPrice)
	{
		Document mapDoc;
		switch (inputPrice) {
		case INCREMENT:
			mapDoc = new Document ("$cond", new Document("if", new Document("$eq",
				Arrays.asList(new Document("$max", Arrays.asList(new Document("$subtract", Arrays.asList("$$candle.c", "$$candle.o")), 0L)), 0L)))
				.append("then", new BsonNull())
				.append("else",new Document("$max", Arrays.asList(new Document("$subtract", Arrays.asList("$$candle.c", "$$candle.o")), 0L))));
			break;
		case DECREMENT:
			mapDoc = new Document ("$cond", new Document("if", new Document("$eq",
				Arrays.asList(new Document("$max", Arrays.asList(new Document("$subtract", Arrays.asList("$$candle.o", "$$candle.c")), 0L)), 0L)))
				.append("then", new BsonNull())
				.append("else",new Document("$max", Arrays.asList(new Document("$subtract", Arrays.asList("$$candle.o", "$$candle.c")), 0L))));
			break;
		case TRUE_RANGE:
			mapDoc = new Document("$max", Arrays.asList(new Document("$subtract", Arrays.asList("$h", "$l")),
				new Document("$abs", new Document("$subtract", Arrays.asList("$h", "$c"))),
				new Document("$abs", new Document("$subtract", Arrays.asList("$l", "$c")))));
			break;
		case TYPICAL:
			mapDoc = new Document("$divide", Arrays.asList(new Document("$sum", Arrays.asList("$h", "$l", "$c")), 3));
			break;
		default:
			return null;
		}
		return new Document("v", mapDoc);
	}

	protected static List<Bson> getMappingStages(InputPrice inputPrice)
	{
		List<Bson> stages = new ArrayList<Bson>();
		stages.add(Aggregates.project(getProjection(inputPrice)));
		stages.add(Aggregates.group(new BsonNull(), getAccumulator(inputPrice)));
		Document mapDoc = getMapDocument(inputPrice);
		if(mapDoc != null)
			stages.add(Aggregates.addFields(new Field<Document>("candles", new Document("$map",
				new Document("input", "$candles")
				.append("as", "candle")
				.append("in", mapDoc)))));
		return stages;
	}
}
