package app.common.net.entities;

public class BrowseReportInfo extends BrowseInfo
{
	private static final long serialVersionUID = 6337231845118804283L;

	protected final String marketId;
	protected final String strategyName;

	public BrowseReportInfo(String strategyName, String marketId, int page, int perPage)
	{
		super(page, perPage);
		this.strategyName = strategyName;
		this.marketId = marketId;
	}

	public BrowseReportInfo(String strategyName, int page, int perPage)
	{
		this(strategyName, null, page, perPage);
	}

	public BrowseReportInfo(String strategyName, String marketId, int page)
	{
		super(page);
		this.strategyName = strategyName;
		this.marketId = marketId;
	}

	public BrowseReportInfo(String strategyName, int page)
	{
		this(strategyName, null, page);
	}

	public BrowseReportInfo(String strategyName, String marketId)
	{
		super();
		this.strategyName = strategyName;
		this.marketId = marketId;
	}

	public BrowseReportInfo(String strategyName)
	{
		this(strategyName, null);
	}

	public String getMarketId()
	{
		return marketId;
	}

	public String getStrategyName()
	{
		return strategyName;
	}
}
