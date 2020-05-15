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
	private double maxDrawdown = Double.NaN; //FIXME: not correctly computed (sometimes gives 0 with a negative net profit)

	public Journal(int granularity, Instant startTime, double initialValue)
	{
		this.granularity = granularity;
		currentTime = startTime;
		currentValue = initialValue;
		hodlTrade = new Trade(startTime, 1.0, initialValue);
	}

	public void setCurrentCandle(Candle candle)
	{
		currentTime = candle.getCloseTime();
		currentValue = candle.getClose();
	}

	public Trade openTrade(double amount)
	{
		if (amount > availAmount())
			throw new IllegalArgumentException("Trying to trade too much (time: " + currentTime + ").");
		Trade trade = new Trade(currentTime, amount, currentValue);
		trades.add(trade);
		totalTrades++;
		investedAmount += amount;
		return trade;
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
		if (trade.profitable()) {
			winningTrades++;
			maxConsecutiveLosing = 0;
			grossProfit += trade.profit();
			if (netProfit > maxProfit)
				currentDrawdown = 0;
		} else {
			maxConsecutiveLosing++;
			grossLoss += trade.profit();
			currentDrawdown += trade.profit();
		}
		if (Double.isNaN(maxDrawdown) || currentDrawdown > maxDrawdown)
			maxDrawdown = currentDrawdown;
		avgAmount += (trade.amount() - avgAmount) / closedTradesCount();
		avgDuration += (trade.duration() - avgDuration) / closedTradesCount();
		investedAmount -= trade.amount();
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
		return 1.0 - investedAmount();
	}

	public Report generateReport()
	{
		hodlTrade.close(currentTime, currentValue, granularity);
		return new Report(netProfit, grossProfit, grossLoss,
			hodlTrade.profit(), totalTrades, openTradesCount(),
			winningTrades, maxConsecutiveLosing, avgAmount, avgDuration,
			Double.isNaN(maxDrawdown) ? 0.0 : maxDrawdown);
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
