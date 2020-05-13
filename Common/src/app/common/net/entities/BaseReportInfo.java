package app.common.net.entities;

public class BaseReportInfo extends Entity
{
	private static final long serialVersionUID = -2889788078149965866L;

	protected String id;
	protected String strategyName;
	protected String market;
	protected double netProfit;

	public BaseReportInfo()
	{
	}

	public BaseReportInfo(String id, String strategyName, String market, double netProfit)
	{
		this.id = id;
		this.strategyName = strategyName;
		this.market = market;
		this.netProfit = netProfit;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getStrategyName()
	{
		return strategyName;
	}

	public void setStrategyName(String strategyName)
	{
		this.strategyName = strategyName;
	}

	public String getMarket()
	{
		return market;
	}

	public void setMarket(String market)
	{
		this.market = market;
	}

	public double getNetProfit()
	{
		return netProfit;
	}

	public void setNetProfit(double netProfit)
	{
		this.netProfit = netProfit;
	}
}
