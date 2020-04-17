package app.datamodel;

import java.util.Arrays;

import org.bson.BsonNull;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import app.datamodel.pojos.DataRange;
import app.datamodel.pojos.MarketData;

public class DataRangeManager extends PojoManager<DataRange>
{

	public DataRangeManager()
	{
		super(DataRange.class);
	}
	
	public DataRange get(String marketId)
	{
		PojoCursor<DataRange> range = aggregate(
				Arrays.asList(
					Aggregates.match(generateFilter(MarketData.class, "market", marketId)),
					Aggregates.sort(generateAscSort(MarketData.class, "start")),
					Aggregates.group(new BsonNull(),
						Accumulators.first("start", "$start"),
						Accumulators.last("end", Filters.eq("$arrayElemAt", Arrays.asList("$candles.t", -1L)))
					)));
		if(range.hasNext())
			return range.next();
		return new DataRange();
	}

}
