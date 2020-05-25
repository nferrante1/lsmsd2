package app.library;

import java.util.ArrayList;
import java.util.List;

import app.library.indicators.Indicator;

public interface ExecutableStrategy
{
	public String name();

	public default List<Indicator> indicators()
	{
		return new ArrayList<Indicator>();
	}

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
