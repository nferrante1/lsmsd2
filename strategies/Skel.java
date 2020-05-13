import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.library.annotations.*;
import app.library.indicators.*;
import app.library.*;

// Strategy Template

public class StrategyName implements ExecutableStrategy
{
	@StrategyParameter
	private String market;
	@StrategyParameter
	private boolean inverseCross;
	@StrategyParameter
	private int granularity;
	@StrategyParameter
	private Instant startTime;
	@StrategyParameter
	private Instant endTime;
	//TODO

	public StrategyName()
	{
		//TODO
	}

	@Override
	public String getName()
	{
		return ""; //TODO
	}

	@Override
	public List<Indicator> indicators()
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		//TODO
		return indicators;
	}

	@Override
	public void init(Journal journal, Map<String, Object> parameters)
	{
		//TODO
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		//TODO
	}

	public void finish(Journal journal)
	{
		//TODO
	}
}
