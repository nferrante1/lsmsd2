package app.common.net.entities;

public class BaseReportInfo extends Entity
{
	private static final long serialVersionUID = -2889788078149965866L;

	protected String market;
	protected String start;
	protected String end;
	protected String author;
	protected boolean deletable;
	protected double netProfit;

	public BaseReportInfo()
	{
	}

	public String getMarket()
	{
		return market;
	}

	public void setMarket(String market)
	{
		this.market = market;
	}

	public String getStart()
	{
		return start;
	}

	public void setStart(String start)
	{
		this.start = start;
	}

	public String getEnd()
	{
		return end;
	}

	public void setEnd(String end)
	{
		this.end = end;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public boolean isDeletable()
	{
		return deletable;
	}

	public void setDeletable(boolean deletable)
	{
		this.deletable = deletable;
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
