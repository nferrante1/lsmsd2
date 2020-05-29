package app.test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import org.bson.conversions.Bson;

import app.datamodel.pojos.DataRange;
import app.library.indicators.SMA;
import app.library.indicators.enums.InputPrice;
import app.server.runner.AggregationRunner;

public class Test {
	
	public static void main (String[] args) {
		SMA sma = new SMA(14, InputPrice.INCREMENT);
		DataRange range = new DataRange();
		range.start = Instant.EPOCH;
		range.end = Instant.now();
		AggregationRunner runner = new AggregationRunner("COINBASE:BTC-EUR", false, 240, range);
		HashMap<String,List<Bson>> map = new HashMap<String, List<Bson>>();
		map.put("SMAu14", sma.pipeline());
		runner.runAggregation(map);
	}
}
