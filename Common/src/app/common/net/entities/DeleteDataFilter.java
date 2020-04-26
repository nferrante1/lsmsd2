package app.common.net.entities;

import java.time.Instant;

public class DeleteDataFilter extends Entity
{
	private static final long serialVersionUID = -7224125556354758993L;

	protected final String marketId;
	protected final Instant date;

	public DeleteDataFilter(String marketId, Instant date)
	{
		this.marketId = marketId;
		this.date = date;
	}

	public DeleteDataFilter(String marketId)
	{
		this(marketId, null);
	}

	public String getMarketId()
	{
		return marketId;
	}

	public Instant getDate()
	{
		return date;
	}
}
