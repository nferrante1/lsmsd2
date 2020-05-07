package app.datamodel.pojos;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("Strategies")
public class Report extends StorablePojo
{
	protected double netProfit;
	protected double grossProfit;
	protected double grossLoss;
	protected double hodlProfit;
	protected int totalTrades;
	protected int openTrades;
	protected int winningTrades;
	protected int maxConsecutiveLosing;
	protected double avgAmount;
	protected double avgDuration;
	protected double maxDrawdown;

	public Report()
	{
		super();
	}

	public Report(double netProfit, double grossProfit, double grossLoss, double hodlProfit, int totalTrades,
		int openTrades, int winningTrades, int maxConsecutiveLosing, double avgAmount, double avgDuration,
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

	public void setTotalTrades(int totalTrades)
	{
		updateField("totalTrades", totalTrades);
	}

	public void setOpenTrades(int openTrades)
	{
		updateField("openTrades", openTrades);
	}

	public void setWinningTrades(int winningTrades)
	{
		updateField("winningTrades", winningTrades);
	}

	public void setMaxConsecutiveLosing(int maxConsecutiveLosing)
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

	public int getTotalTrades()
	{
		return totalTrades;
	}

	public int getOpenTrades()
	{
		return openTrades;
	}

	public int getWinningTrades()
	{
		return winningTrades;
	}

	public int getMaxConsecutiveLosing()
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
