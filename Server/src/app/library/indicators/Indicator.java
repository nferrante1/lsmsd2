package app.library.indicators;

import java.util.ArrayList;
import java.util.List;

import app.library.Candle;

public abstract class Indicator
{
	public List<Indicator> depends()
	{
		return new ArrayList<Indicator>();
	}

	abstract public void compute(Candle candle);
}
