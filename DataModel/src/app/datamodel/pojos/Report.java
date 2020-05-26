package app.datamodel.pojos;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("Strategies")
public final class Report extends StorablePojo
{
	private double netProfit;
	private double grossProfit;
	private double grossLoss;
	private double hodlProfit;
	private long totalTrades;
	private long openTrades;
	private long winningTrades;
	private long maxConsecutiveLosing;
	private double avgAmount;
	private double avgDuration;
	private double maxDrawdown;

	public Report()
	{
		super();
	}

	public Report(double netProfit, double grossProfit, double grossLoss, double hodlProfit, long totalTrades,
		long openTrades, long winningTrades, long maxConsecutiveLosing, double avgAmount, double avgDuration,
		double maxDrawdown)
	{
		super(StorablePojoState.UNTRACKED);
		this.netProfit = netProfit;
		this.grossProfit = grossProfit;
		this.grossLoss = grossLoss;
		this.hodlProfit = hodlProfit;
		this.totalTrades = totalTrades;
		this.openTrades = openTrades;
		this.winningTrades = winningTrades;
		this.maxConsecutiveLosing = maxConsecutiveLosing;
		this.avgAmount = avgAmount;
		this.avgDuration = avgDuration;
		this.maxDrawdown = maxDrawdown;
	}

	public void setNetProfit(double netProfit)
	{
		updateField("netProfit", netProfit);
	}

	public void setGrossProfit(double grossProfit)
	{
		updateField("grossProfit", grossProfit);
	}

	public void setGrossLoss(double grossLoss)
	{
		updateField("grossLoss", grossLoss);
	}

	public void setHodlProfit(double hodlProfit)
	{
		updateField("hodlProfit", hodlProfit);
	}

	public void setTotalTrades(long totalTrades)
	{
		updateField("totalTrades", totalTrades);
	}

	public void setOpenTrades(long openTrades)
	{
		updateField("openTrades", openTrades);
	}

	public void setWinningTrades(long winningTrades)
	{
		updateField("winningTrades", winningTrades);
	}

	public void setMaxConsecutiveLosing(long maxConsecutiveLosing)
	{
		updateField("maxConsecutiveLosing", maxConsecutiveLosing);
	}

	public void setAvgAmount(double avgAmount)
	{
		updateField("avgAmount", avgAmount);
	}

	public void setAvgDuration(double avgDuration)
	{
		updateField("avgDuration", avgDuration);
	}

	public void setMaxDrawdown(double maxDrawdown)
	{
		updateField("maxDrawdown", maxDrawdown);
	}

	public double getNetProfit()
	{
		return netProfit;
	}

	public double getGrossProfit()
	{
		return grossProfit;
	}

	public double getGrossLoss()
	{
		return grossLoss;
	}

	public double getHodlProfit()
	{
		return hodlProfit;
	}

	public long getTotalTrades()
	{
		return totalTrades;
	}

	public long getOpenTrades()
	{
		return openTrades;
	}

	public long getWinningTrades()
	{
		return winningTrades;
	}

	public long getMaxConsecutiveLosing()
	{
		return maxConsecutiveLosing;
	}

	public double getAvgAmount()
	{
		return avgAmount;
	}

	public double getAvgDuration()
	{
		return avgDuration;
	}

	public double getMaxDrawdown()
	{
		return maxDrawdown;
	}
}
