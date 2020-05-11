package app.server.managers;

import java.util.ArrayList;
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

	public PojoCursor<MarketInfo> findMarketInfo(String sourceName, String marketName, boolean onlySelectable, int pageNumber, int perPage)
	{
		List<Bson> stages = new ArrayList<Bson>();
		stages.add(Aggregates.unwind("$markets"));

		List<Bson> matchFilters = new ArrayList<Bson>();
		if (sourceName != null)
			matchFilters.add(Filters.regex("_id", Pattern.compile(sourceName, Pattern.CASE_INSENSITIVE)));
		if (marketName != null)
			matchFilters.add(Filters.regex("markets.id", Pattern.compile(marketName, Pattern.CASE_INSENSITIVE)));
		if (onlySelectable) {
			matchFilters.add(Filters.eq("enabled", true));
			matchFilters.add(Filters.eq("markets.selectable", true));
		}
		if (!matchFilters.isEmpty())
			stages.add(Aggregates.match(matchFilters.size() == 1 ? matchFilters.get(0) : Filters.and(matchFilters)));

		stages.add(Aggregates.project(Projections.fields(
			Projections.excludeId(),
			Projections.computed("marketId", "$markets.id"),
			Projections.computed("baseCurrency", "$markets.baseCurrency"),
			Projections.computed("quoteCurrency", "$markets.quoteCurrency"),
			Projections.computed("sourceName", "$_id"),
			Projections.computed("granularity", "$markets.granularity"), Projections.computed("selectable", "$markets.selectable"),
			Projections.computed("sync", "$markets.sync")
			)));
		stages.add(Aggregates.sort(Sorts.ascending("sourceName", "marketId")));
		stages.add(Aggregates.skip((pageNumber - 1)*perPage));
		stages.add(Aggregates.limit(perPage));

		return aggregate(stages);
	}
}
