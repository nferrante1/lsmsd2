package app.library;

import java.util.ArrayList;
import java.util.List;

import app.library.indicators.Indicator;
import app.library.indicators.RSI;

public class SampleStrategy implements ExecutableStrategy {

	RSI rsi;
	@Override
	public String getName()
	{
		return "SampleStrategy"
	}

	@Override
	public void process(Candle candle)
	{
		System.out.println("" + candle.getOpen(),
				"" + candle.getHigh()(),
				"" + candle.getLow(),
				"" + candle.getClose(),
				"" + candle.getVolume()
		);
	}

	public SampleStrategy() {
		rsi = new RSI(14);
	};
	@Override
	public List<Indicator> getIndicators()
	{
		List<Indicator> ind = new ArrayList<Indicator>();
		ind.add(rsi);
		return ind;
	}

}
