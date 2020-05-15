package app.library;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import app.datamodel.pojos.Report;

public final class Journal
{
	private List<Trade> trades = new ArrayList<Trade>();
	private int granularity;
	private double investedAmount;
	private double totalAmount;
	private Instant currentTime;
	private Trade hodlTrade;
	private double currentValue;
	private double netProfit;
	private double maxProfit = Double.MIN_VALUE;
	private double grossProfit;
	private double grossLoss;
	private long totalTrades;
	private long winningTrades;
	private long maxConsecutiveLosing;
	private double avgAmount;
	private double avgDuration;
	private double currentDrawdown;
	private double maxAmount;
	private double maxDrawdown; 
	private final double MIN_TRADEABLE = 1e-8;

	public Journal(int granularity, Instant startTime, double initialValue)
	{
		this.granularity = granularity;
		currentTime = startTime;
		currentValue = initialValue;
		hodlTrade = new Trade(startTime, 1.0, initialValue);
		totalAmount = 1.0;
		maxAmount = 1.0;
	}

	public void setCurrentCandle(Candle candle)
	{
		currentTime = candle.getCloseTime();
		currentValue = candle.getClose();
	}
	
	public double getTotalAmount(){
		return roundDown(totalAmount);
	}
	
	public boolean hasAmount() {
		return (getTotalAmount() > 0);
	}

	public Trade openTrade(double amount)
	{
		amount = roundDown(amount);
		if (amount > availAmount())
			throw new IllegalArgumentException("Trying to trade too much (time: " + currentTime + ").");
		Trade trade = new Trade(currentTime, amount, currentValue);
		trades.add(trade);
		totalTrades++;
		investedAmount += amount;
		return trade;
	}
	
	public double roundDown(double amount) {
		return amount - (amount % MIN_TRADEABLE);
	}

	public Trade allIn()
	{
		return openTrade(availAmount());
	}

	public Trade closeTrade(Trade trade)
	{
		if (!trades.contains(trade))
			throw new IllegalArgumentException("Trade not registered (time: " + currentTime + ").");
		if (trade.closed())
			throw new IllegalStateException("Trade already closed (time: " + currentTime + ").");
		if (!trade.entryTime().isBefore(currentTime))
			throw new IllegalArgumentException("Trying to open and close the same trade during a single day (time: " + currentTime + ").");
		
		trade.close(currentTime, currentValue, granularity);
		trades.remove(trade);
		netProfit += trade.profit();
		totalAmount += trade.profit();
		
		if(totalAmount > maxAmount){
			maxAmount = totalAmount;
			currentDrawdown = 0;
		}		
		
		if (trade.profitable()) {
			winningTrades++;
			maxConsecutiveLosing = 0;
			grossProfit += trade.profit();
		} else {
			maxConsecutiveLosing++;
			grossLoss += trade.profit();
			currentDrawdown += trade.profit();
		}
				
		avgAmount += (trade.amount() - avgAmount) / closedTradesCount();
		avgDuration += (trade.duration() - avgDuration) / closedTradesCount();
		investedAmount -= trade.amount();
		if(currentDrawdown < maxDrawdown)
			maxDrawdown = currentDrawdown;
		return trade;
	}

	public List<Trade> openTrades()
	{
		return new ArrayList<Trade>(trades);
	}

	public void closeAll()
	{
		for (Trade trade: openTrades())
			closeTrade(trade);
	}

	public Trade oldestTrade()
	{
		if (hasOpenTrades())
			return null;
		return trades.get(0);
	}

	public Trade getTrade(int index)
	{
		if (index >= openTradesCount())
			return null;
		return trades.get(index);
	}

	public boolean hasOpenTrades()
	{
		return openTradesCount() > 0;
	}

	public Trade lastOpenTrade()
	{
		return trades.get(trades.size() - 1);
	}

	public long openTradesCount()
	{
		return trades.size();
	}

	public long closedTradesCount()
	{
		return totalTrades - openTradesCount();
	}

	public long tradesCount()
	{
		return totalTrades;
	}

	public double investedAmount()
	{
		return investedAmount;
	}

	public double availAmount()
	{
		return roundDown(totalAmount - investedAmount());
	}

	public Report generateReport()
	{
		hodlTrade.close(currentTime, currentValue, granularity);
		return new Report(netProfit, grossProfit, grossLoss,
			hodlTrade.profit(), totalTrades, openTradesCount(),
			winningTrades, maxConsecutiveLosing, avgAmount, avgDuration,
			maxDrawdown);
	}

	public Instant getCurrentTime()
	{
		return currentTime;
	}

	public double getCurrentValue()
	{
		return currentValue;
	}
}
