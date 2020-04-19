package app.server;

import java.util.Arrays;

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
	
	public PojoCursor<MarketInfo> getMarketInfo(String filter, int pageNumber, int perPage )
	{
		
		return aggregate(Arrays.asList(
				Aggregates.unwind("$markets"),
				Aggregates.match(Filters.regex("markets.id", filter)),
				Aggregates.project(
						Projections.fields(Arrays.asList(
								Projections.excludeId(),
								Projections.computed("id", Filters.eq("$concat", Arrays.asList("$_id",":","$markets.id"))),
								Projections.computed("granularity", "$markets.granularity"), Projections.computed("selectable", "$markets.selectable"),
								Projections.computed("sync", "$markets.sync")))),
				Aggregates.sort(Sorts.ascending("id")),
				Aggregates.skip((pageNumber-1)*perPage),
				Aggregates.limit(perPage)
				));
	}
	
}
