package app.common.net.entities;

public class ReportInfo extends BaseReportInfo
{
	private static final long serialVersionUID = 7958042541743504580L;

	protected String user;
	protected double grossProfit;
	protected double grossLoss;
	protected double hodlProfit;
	protected double totalTrades;
	protected double openTrades;
	protected double winningTrades;
	protected double maxConsecutiveLosing;
	protected double avgAmount;
	protected double avgDuration;
	protected double maxDrawdown;
	protected boolean deletable;

	public ReportInfo()
	{
		super();
	}

	public double getGrossProfit()
	{
		return grossProfit;
	}

	public void setGrossProfit(double grossProfit)
	{
		this.grossProfit = grossProfit;
	}

	public double getGrossLoss()
	{
		return grossLoss;
	}

	public void setGrossLoss(double grossLoss)
	{
		this.grossLoss = grossLoss;
	}

	public double getHodlProfit()
	{
		return hodlProfit;
	}

	public void setHodlProfit(double hodlProfit)
	{
		this.hodlProfit = hodlProfit;
	}

	public double getTotalTrades()
	{
		return totalTrades;
	}

	public void setTotalTrades(double totalTrades)
	{
		this.totalTrades = totalTrades;
	}

	public double getOpenTrades()
	{
		return openTrades;
	}

	public void setOpenTrades(double openTrades)
	{
		this.openTrades = openTrades;
	}

	public double getWinningTrades()
	{
		return winningTrades;
	}

	public void setWinningTrades(double winningTrades)
	{
		this.winningTrades = winningTrades;
	}

	public double getMaxConsecutiveLosing()
	{
		return maxConsecutiveLosing;
	}

	public void setMaxConsecutiveLosing(double maxConsecutiveLosing)
	{
		this.maxConsecutiveLosing = maxConsecutiveLosing;
	}

	public double getMaxDrawdown()
	{
		return maxDrawdown;
	}

	public void setMaxDrawdown(double maxDrawdown)
	{
		this.maxDrawdown = maxDrawdown;
	}

	public double getAvgAmount()
	{
		return avgAmount;
	}

	public void setAvgAmount(double avgAmount)
	{
		this.avgAmount = avgAmount;
	}

	public double getAvgDuration()
	{
		return avgDuration;
	}

	public void setAvgDuration(double avgDuration)
	{
		this.avgDuration = avgDuration;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public boolean isDeletable()
	{
		return deletable;
	}

	public void setDeletable(boolean deletable)
	{
		this.deletable = deletable;
	}
}
