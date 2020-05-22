package app.library.indicators;

import java.util.Arrays;
import java.util.List;

import app.library.Candle;

public class MACD extends Indicator {

	private int shortPeriod;
	private int longPeriod;
	private EMA shortEMA;
	private EMA longEMA;
	private double value = Double.NaN;
	
	public MACD() {
		this(12,26);
	}
	public MACD(int shortPeriod,int longPeriod) {
		this.longPeriod = longPeriod;
		this.shortPeriod = shortPeriod;
		this.longEMA = new EMA(longPeriod);
		this.shortEMA = new EMA(shortPeriod);
	}
	
	@Override
	public void compute(Candle candle)
	{
		longEMA.compute(candle);
		shortEMA.compute(candle);
		double lema = longEMA.getValue();
		double sema = shortEMA.getValue();
		if(Double.isNaN(sema)|| Double.isNaN(lema))
			value = Double.NaN;
		else
			value = sema - lema; 
	}

	@Override
	public List<Indicator> depends(){
		return Arrays.asList(longEMA, shortEMA);
	}
	public int getShortPeriod()
	{
		return shortPeriod;
	}
	public int getLongPeriod()
	{
		return longPeriod;
	}
	public EMA getShortEMA()
	{
		return shortEMA;
	}
	public EMA getLongEMA()
	{
		return longEMA;
	}
	public double getValue()
	{
		return value;
	}
}
