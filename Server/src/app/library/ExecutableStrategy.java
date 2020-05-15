package app.library;

import java.util.List;

import app.library.indicators.Indicator;

public interface ExecutableStrategy
{
	public String getName();
	public List<Indicator> indicators();

	public default boolean validate()
	{
		return true;
	}

	public default void init(Journal journal)
	{
	}

	public void process(Journal journal, Candle candle);

	public default void finish(Journal journal)
	{
	}
}
