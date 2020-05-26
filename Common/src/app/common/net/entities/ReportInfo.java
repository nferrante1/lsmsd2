package app.common.net.entities;

public final class ReportInfo extends BaseReportInfo
{
	private static final long serialVersionUID = 7958042541743504580L;

	private final String user;
	private final double grossProfit;
	private final double grossLoss;
	private final double hodlProfit;
	private final long totalTrades;
	private final long openTrades;
	private final long winningTrades;
	private final long maxConsecutiveLosing;
	private final double avgAmount;
	private final double avgDuration;
	private final double maxDrawdown;
	private final boolean deletable;

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

	public double getMaxDrawdown()
	{
		return maxDrawdown;
	}

	public double getAvgAmount()
	{
		return avgAmount;
	}

	public double getAvgDuration()
	{
		return avgDuration;
	}

	public String getUser()
	{
		return user;
	}

	public boolean isDeletable()
	{
		return deletable;
	}

	public long getClosedTrades()
	{
		return totalTrades - openTrades;
	}

	public long getLosingTrades()
	{
		return getClosedTrades() - winningTrades;
	}
}
