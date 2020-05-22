package app.server.managers;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.common.net.entities.BaseReportInfo;
import app.datamodel.PojoCursor;
import app.datamodel.PojoManager;

public class BaseReportInfoManager extends PojoManager<BaseReportInfo> {

	public BaseReportInfoManager()
	{
		super(BaseReportInfo.class, "Strategies");
	}
	
	public  PojoCursor<BaseReportInfo> getBaseReportInfo(String strategyName, String marketId, int page, int perPage) {
		
		List<Bson> stages = new ArrayList<Bson>();
		if(strategyName != null)
			stages.add(Aggregates.match(Filters.eq("name", strategyName)));
		stages.add(Aggregates.unwind("$runs"));
		if(marketId != null)
			stages.add(Aggregates.match(Filters.eq("runs.parameters.market", marketId)));
		
		
		stages.add(Aggregates.sort(Sorts.descending("runs.report.netProfit")));
		stages.add(Aggregates.project(Projections.fields(
				Projections.excludeId(), 
				Projections.computed("strategyName", "$name"),
				Projections.computed("market", "$runs.parameters.market"),
				Projections.computed("netProfit", "$runs.report.netProfit"),
				Projections.computed("id", new Document("$toString", "$runs.id")))));
		
		return aggregate(stages);

	}
}
