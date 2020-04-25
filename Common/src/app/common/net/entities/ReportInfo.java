package app.common.net.entities;

public class ReportInfo extends Entity
{
	private static final long serialVersionUID = 7958042541743504580L;

	protected String Marketname;
	protected String start;
	protected String end;
	protected String author;
	protected boolean canDelete;

	protected double netProfit;
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

	public ReportInfo()
	{
	}

	public ReportInfo(String Marketname, String start, String end, String author, boolean canDelete)
	{
		this.Marketname = Marketname;
		this.start = start;
		this.end = end;
		this.author = author;
		this.canDelete = canDelete;
	}

	public String getStart()
	{
		return start;
	}

	public String getEnd()
	{
		return end;
	}

	public String getAuthor()
	{
		return author;
	}

	public boolean isCanDelete()
	{
		return canDelete;
	}

	public String getMarketname()
	{
		return Marketname;
	}

	public void setMarketname(String marketname)
	{
		Marketname = marketname;
	}

	public double getNetProfit()
	{
		return netProfit;
	}

	public void setNetProfit(double netProfit)
	{
		this.netProfit = netProfit;
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

	public void setStart(String start)
	{
		this.start = start;
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

	public void setEnd(String end)
	{
		this.end = end;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public void setCanDelete(boolean canDelete)
	{
		this.canDelete = canDelete;
	}
}
