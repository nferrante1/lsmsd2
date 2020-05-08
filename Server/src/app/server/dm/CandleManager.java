package app.server.dm;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.bson.Document;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
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

	public PojoCursor<Candle> getCandles(String marketId) //TODO
	{
		return aggregate(Aggregates.match(Filters.eq("market", "ETH-EUR")), //Sostituire ETH-EUR con marketid
				Aggregates.sort(Sorts.ascending("start")),
				Aggregates.unwind("$candles"),
				Aggregates.replaceRoot("$candles"),
				Aggregates.addFields(new Field<Document>("n",
					new Document("$floor", new Document("$divide", Arrays.asList(new Document("$subtract", Arrays.asList("$t",
						new SimpleDateFormat("EEE MMMMM dd yyyy HH:mm:ss").format(new java.util.Date(0L)))), 1200000L))))), //granularity
				Aggregates.group("$n",
					Accumulators.first("t", "$t"),
					Accumulators.first("o", "$o"),
					Accumulators.max("h", "$h"),
					Accumulators.min("l", "$l"),
					Accumulators.last("c", "$c"),
					Accumulators.sum("v", "$v")
				)
			);
	}
}
