package app.library;

import java.util.List;
import java.util.Map;

import app.library.indicators.Indicator;

public interface ExecutableStrategy
{
	public String getName();
	public List<Indicator> indicators();

	public default void init(Journal journal, Map<String, Object> parameters)
	{
	}

	public void process(Journal journal, Candle candle);

	public default void finish(Journal journal)
	{
	}
}
