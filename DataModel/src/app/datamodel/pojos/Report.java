package app.datamodel.pojos;

public class Report extends Pojo {
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

	
	public Report(double np, double gp, double gl, double hp, int tt, int ot, int wt, int mcl, double aa, double ad, double md)
	{
		netProfit = np;
		grossProfit = gp;
		grossLoss = gl;
		hodlProfit = hp;
		totalTrades = tt;
		openTrades = ot;
		winningTrades = wt;
		maxConsecutiveLosing = mcl;
		avgAmount = aa;
		avgDuration = ad;
		maxDrawdown = md;
	}
	
	
	public void setNetProfit(double netProfit)
	{
		this.netProfit = netProfit;
	}
	public void setGrossProfit(double grossProfit)
	{
		this.grossProfit = grossProfit;
	}
	public void setGrossLoss(double grossLoss)
	{
		this.grossLoss = grossLoss;
	}
	public void setHodlProfit(double hodlProfit)
	{
		this.hodlProfit = hodlProfit;
	}
	public void setTotalTrades(int totalTrades)
	{
		this.totalTrades = totalTrades;
	}
	public void setOpenTrades(int openTrades)
	{
		this.openTrades = openTrades;
	}
	public void setWinningTrades(int winningTrades)
	{
		this.winningTrades = winningTrades;
	}
	public void setMaxConsecutiveLosing(int maxConsecutiveLosing)
	{
		this.maxConsecutiveLosing = maxConsecutiveLosing;
	}
	public void setAvgAmount(double avgAmount)
	{
		this.avgAmount = avgAmount;
	}
	public void setAvgDuration(double avgDuration)
	{
		this.avgDuration = avgDuration;
	}
	public void setMaxDrawdown(double maxDrawdown)
	{
		this.maxDrawdown = maxDrawdown;
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
