package app.library;

import java.util.List;

import app.library.indicators.Indicator;

public interface ExecutableStrategy
{
	public String getName();
	public void process(Candle candle);
	public List<Indicator> getIndicators();
}
