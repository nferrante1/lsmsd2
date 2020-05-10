import java.util.ArrayList;
import java.util.List;

import app.library.ExecutableStrategy;
import app.library.indicators.Indicator;
import app.library.indicators.RSMA;
import app.library.Candle;

public class RSMAStrategy implements ExecutableStrategy
{
	RSMA rsma;
	@Override
	public String getName()
	{
		return "RSMAStrategy";
	}

	@Override
	public void process(Candle candle)
	{
		System.out.println("T: " + candle.getTime()
			+ " | O: " + candle.getOpen()
			+ " | H: " + candle.getHigh()
			+ " | L: " + candle.getLow()
			+ " | C: " + candle.getClose()
			+ " | V: " + candle.getVolume()
			+ " | RSMAu14: " + rsma.getValue()
		);
	}

	public RSMAStrategy()
	{
		rsma = new RSMA(14, true);
	}

	@Override
	public List<Indicator> getIndicators()
	{
		List<Indicator> ind = new ArrayList<Indicator>();
		ind.add(rsma);
		return ind;
	}

}
