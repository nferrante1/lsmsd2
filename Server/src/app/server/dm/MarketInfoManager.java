package app.server.dm;

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

	public PojoCursor<MarketInfo> getMarketInfo(String filter, int pageNumber, int perPage)
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
		/*
		+		List<Bson> stages = new ArrayList<Bson>();
		+		if(dataSource!= null && !dataSource.isEmpty())
		+			stages.add(Aggregates.match(Filters.regex("_id", Pattern.compile(dataSource, Pattern.CASE_INSENSITIVE))));
		+		stages.add(Aggregates.unwind("$markets"));
		+		if(marketName!=null && !marketName.isEmpty())
		+			stages.add(Aggregates.match(Filters.regex("markets.id", Pattern.compile(marketName, Pattern.CASE_INSENSITIVE))));
		+		stages.add(Aggregates.project(
		+				Projections.fields(Arrays.asList(
		+						Projections.excludeId(),
		+						Projections.computed("id", Filters.eq("$concat", Arrays.asList("$_id",":","$markets.id"))),
		+						Projections.computed("granularity", "$markets.granularity"), Projections.computed("selectable", "$markets.selectable"),
		+						Projections.computed("sync", "$markets.sync"))))
		+				);
		+		stages.add(Aggregates.sort(Sorts.ascending("id")));
		+		stages.add(Aggregates.skip((pageNumber-1)*perPage));
		+		stages.add(Aggregates.limit(perPage));
		+		return aggregate(stages);
		*/
	}
}
