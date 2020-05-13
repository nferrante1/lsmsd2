package app.common.net.entities;

public class ReportInfo extends BaseReportInfo
{
	private static final long serialVersionUID = 7958042541743504580L;

	protected String user;
	protected double grossProfit;
	protected double grossLoss;
	protected double hodlProfit;
	protected long totalTrades;
	protected long openTrades;
	protected long winningTrades;
	protected long maxConsecutiveLosing;
	protected double avgAmount;
	protected double avgDuration;
	protected double maxDrawdown;
	protected boolean deletable;

	public ReportInfo()
	{
		super();
	}

	public ReportInfo(String id, String strategyName, String market, double netProfit,
		String user, double grossProfit, double grossLoss, double hodlProfit,
		long totalTrades, long openTrades, long winningTrades, long maxConsecutiveLosing,
		double avgAmount, double avgDuration, double maxDrawdown, boolean deletable)
	{
		super(id, strategyName, market, netProfit);
		this.user = user;
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
		this.deletable = deletable;
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

	public long getTotalTrades()
	{
		return totalTrades;
	}

	public void setTotalTrades(long totalTrades)
	{
		this.totalTrades = totalTrades;
	}

	public long getOpenTrades()
	{
		return openTrades;
	}

	public void setOpenTrades(long openTrades)
	{
		this.openTrades = openTrades;
	}

	public long getWinningTrades()
	{
		return winningTrades;
	}

	public void setWinningTrades(long winningTrades)
	{
		this.winningTrades = winningTrades;
	}

	public long getMaxConsecutiveLosing()
	{
		return maxConsecutiveLosing;
	}

	public void setMaxConsecutiveLosing(long maxConsecutiveLosing)
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
