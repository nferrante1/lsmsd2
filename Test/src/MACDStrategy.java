import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import app.library.annotations.*;
import app.library.indicators.enums.*;
import app.library.indicators.*;
import app.library.*;

// Strategy Template

/*
 * Order of execution of methods when the strategy is
 * uploaded:
 * constructor > name()
 * Order of execution of methods when the strategy is
 * executed:
 * constructor > (parameters setup) > validate()
 *     > indicators() > init() > process() (for each candle)
 *     > finish()
 */

// TODO: set ClassName
public class MACDStrategy implements ExecutableStrategy
{
	/*
	 * The following parameters are automatically set
	 * when the strategy is loaded for the execution
	 * (after the call to validate() and before the
	 * call to indicators()).
	 */
	@StrategyParameter
	private String market;
	@StrategyParameter
	private boolean inverseCross;
	@StrategyParameter
	private int granularity;
	@StrategyParameter
	private Instant startTime;
	@StrategyParameter
	private Instant endTime;
	@StrategyParameter("Long EMA Period")
	private int longEMAPeriod;
	@StrategyParameter("Short EMA Period")
	private int shortEMAPeriod;
	@StrategyParameter("MACD Period")
	private int MACDPeriod;
	
	private MACD macd;
	private double previousMACD; 
	
	/*
	 * TODO: add here additional parameters. To define a
	 * parameter, use the @StrategyParameter annotation.
	 * The annotation supports an optional string value
	 * to define the text that will be shown to the user
	 * when he is asked to provide the value for the
	 * parameter. Example:
	 * @StrategyParameter("RSI Oversold threshold (0-100)")
	 * private int rsiOversold;
	 */

	@Override
	public String name()
	{
		/*
		 * TODO: set here the name of the strategy.
		 * This method will be called when the
		 * strategy is uploaded to the server in
		 * order to get the name.
		 */
		return "MACD strategy";
	}

	@Override
	public boolean validate()
	{
		/*
		 * TODO: validate the parameters sets by the
		 * user. Return true if they are valid,
		 * false otherwise. Example:
		 * return rsiOversold > 0 && rsiOversold <= 100;
		 */
		return longEMAPeriod > 0 && shortEMAPeriod > 0 && MACDPeriod > 0;
	}

	@Override
	public List<Indicator> indicators() // Optional
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		macd = new MACD(shortEMAPeriod, longEMAPeriod, MACDPeriod);
		indicators.add(macd);
		/*
		 * TODO: create here the list of indicators
		 * used by the strategy. Save the indicator
		 * instances in class fields in order to
		 * access the values later in the process()
		 * method. Example:
		 * rsiIndicator = new RSI(14);
		 * indicators.add(rsiIndicator);
		 */
		return indicators;
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		/*
		 * TODO: this method will be called on
		 * every candle (also the first candle). If
		 * you open a trade here, it will be open
		 * when the candle is closed (the price
		 * that can be accessed throught the
		 * `candle` argument is the price at the
		 * close of the candle).
		 * To open a trade:
		 * Trade trade = journal.openTrade(amount);
		 * To close a trade:
		 * journal.closeTrade(trade);
		 * Note that you can not open and close the
		 * same trade in the same trading day.
		 * Other useful methods can be found in the
		 * following classes:
		 * app.library.Journal
		 * app.library.Trade
		 * app.library.Candle
		 */
		
		double value = macd.hist();
		if(Double.isNaN(value))
			return;
		if(Double.isNaN(previousMACD))
		{
			previousMACD = value;
			return;
		}
		
		if(previousMACD <= 0 && value > 0)
			journal.allIn();
		if(previousMACD >= 0 && value < 0)
			journal.closeAll();
		
		previousMACD = value;
	}
}
