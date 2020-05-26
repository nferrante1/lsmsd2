package app.datamodel.pojos;

import java.time.Instant;

import app.datamodel.pojos.annotations.CollectionName;

@CollectionName("MarketData")
public final class DataRange
{
	public Instant start;
	public Instant end;

	public DataRange()
	{
	}

	public DataRange(Instant start, Instant end)
	{
		this.start = start;
		this.end = end;
	}
}
