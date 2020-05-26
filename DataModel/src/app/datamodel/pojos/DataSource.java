package app.datamodel.pojos;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import app.datamodel.pojos.annotations.CollectionName;
import app.datamodel.pojos.enums.StorablePojoState;

@CollectionName("Sources")
public final class DataSource extends StorablePojo
{
	@BsonId
	private String name;
	private boolean enabled;
	private List<Market> markets = new ArrayList<Market>();

	public DataSource()
	{
		super();
	}

	public DataSource(String name)
	{
		super(StorablePojoState.UNTRACKED);
		this.name = name;
		enabled = true;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		updateField("name", name);
	}

	public void setEnabled(boolean enabled)
	{
		updateField("enabled", enabled);
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	@BsonIgnore
	public Market getMarket(String marketId)
	{
		for (Market market: markets)
			if (market.getId().equals(marketId))
				return market;
		return null;
	}

	public List<Market> getMarkets()
	{
		return markets;
	}

	public void addMarket(Market market)
	{
		markets.add(market);
	}

	public void setMarkets(List<Market> markets)
	{
		this.markets = markets;
	}
}
