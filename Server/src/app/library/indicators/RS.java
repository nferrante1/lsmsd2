package app.library.indicators;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import app.library.Candle;

public class RS extends Indicator {
	private int period;
	private RSMA rsmau; 
	private RSMA rsmad;
	private double value;
	
	public RS(int period) {
		this.rsmau = new RSMA(period, true);
		this.rsmad = new RSMA(period, false);
		this.period = period;
	}

	@Override
	public List<Indicator> depends(){
		List<Indicator> indicators = new ArrayList<Indicator>();
		indicators.add(rsmau);
		indicators.add(rsmad);
		return indicators;
	}
	@Override
	public void compute(Candle candle)
	{
		rsmau.compute(candle);
		rsmad.compute(candle);
		value = rsmau.getValue() / rsmad.getValue();
		
	}
	
	public double getValue() {
		return this.value;
	}

}
