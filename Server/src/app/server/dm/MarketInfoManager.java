package app.server.dm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.common.net.entities.MarketInfo;
import app.datamodel.PojoCursor;
import app.datamodel.PojoManager;

public class MarketInfoManager extends PojoManager<MarketInfo>
{
	public MarketInfoManager()
	{
		super(MarketInfo.class, "Sources");
	}

	
	
//	public PojoCursor<MarketInfo> getMarketInfo(int pageNumber, int perPage)
//	{
//		return aggregate(Arrays.asList(
//				Aggregates.unwind("$markets"),
//				Aggregates.project(
//						Projections.fields(Arrays.asList(
//								Projections.excludeId(),
//								Projections.computed("marketId", "$markets.id"),
//								Projections.computed("baseCurrency", "$markets.baseCurrency"),
//								Projections.computed("quoteCurrency", "$markets.quoteCurrency"),
//								Projections.computed("sourceName", "$_id"),
//								Projections.computed("granularity", "$markets.granularity"), Projections.computed("selectable", "$markets.selectable"),
//								Projections.computed("sync", "$markets.sync")))),
//				
//				Aggregates.sort(Sorts.ascending("id")),
//				Aggregates.skip((pageNumber-1)*perPage),
//				Aggregates.limit(perPage)
//				));
//	}
//	
	public PojoCursor<MarketInfo> getMarketInfo(String sourceName, String marketName, int pageNumber, int perPage)
	{
		List<Bson> stages = new ArrayList<Bson>();
		stages.add(Aggregates.unwind("$markets"));
		if(sourceName != null && marketName != null)
			stages.add(Aggregates.match(Filters.and(
					Filters.eq("_id", sourceName), 
					Filters.regex("markets.id", Pattern.compile(marketName, Pattern.CASE_INSENSITIVE)))
				));
		else if(sourceName != null)
			stages.add(Aggregates.match(Filters.eq("_id", sourceName)));
		else if(marketName != null)
			stages.add(Aggregates.match(
					Filters.regex("markets.id", Pattern.compile(marketName, Pattern.CASE_INSENSITIVE)))
					);
		stages.add(Aggregates.project(
						Projections.fields(Arrays.asList(
								Projections.excludeId(),
								Projections.computed("marketId", "$markets.id"),
								Projections.computed("baseCurrency", "$markets.baseCurrency"),
								Projections.computed("quoteCurrency", "$markets.quoteCurrency"),
								Projections.computed("sourceName", "$_id"),
								Projections.computed("granularity", "$markets.granularity"), Projections.computed("selectable", "$markets.selectable"),
								Projections.computed("sync", "$markets.sync")))));
		stages.add(Aggregates.sort(Sorts.ascending("sourceName", "marketId")));
		stages.add(Aggregates.skip((pageNumber-1)*perPage));
		stages.add(Aggregates.limit(perPage));
		return aggregate(stages);
	}
	
}
