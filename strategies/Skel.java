import java.util.ArrayList;
import java.util.List;

import app.library.indicators.*;
import app.library.*;

// Strategy Template

public class StrategyName implements ExecutableStrategy
{
	private String market;
	private boolean inverseCross;
	private int granularity;
	//TODO

	@Override
	public String getName()
	{
		return ""; //TODO
	}

	@Override
	public void process(Candle candle)
	{
		//TODO
	}

	public RSMAStrategy()
	{
		//TODO
	}

	@Override
	public List<Indicator> getIndicators()
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		//TODO
		return indicators;
	}

}
