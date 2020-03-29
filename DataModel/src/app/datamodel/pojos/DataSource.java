package app.datamodel.pojos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import com.google.gson.annotations.SerializedName;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.EmbeddedPojoManager;

@CollectionName("Sources")
public class DataSource extends Pojo
{
	@BsonId
	protected String name;
	protected boolean enabled;
	protected List<Market> markets = new ArrayList<Market>();
	
	public DataSource()
	{
		super();
	}
	public DataSource(String name)
	{
		super();
		this.name = name;
		enabled = true;
	}
	
	
	/*@Override
	protected void postLoad()
	{
		super.postLoad();
		for (Market market: markets)
			market.setContainer(this);
	}*/
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void setMarkets(List<Market> markets)
	{
		for(Market market: markets)
			market.setContainer(this);
		this.markets = markets;
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
		market.setContainer(this);
		markets.add(market);
		EmbeddedPojoManager<Market> manager = new EmbeddedPojoManager<Market>(Market.class);
		manager.insert(market);
		
	}
	
	public void removeMarket(String marketId)
	{
		Iterator<Market> marketsIterator = markets.iterator();
		while (marketsIterator.hasNext()) {
			Market market = marketsIterator.next();
			if (market.getId().equals(marketId)) {
				marketsIterator.remove();
				EmbeddedPojoManager<Market> manager = new EmbeddedPojoManager<Market>(Market.class);
				manager.delete(market);
				break;
			}
		}
	}
	
	public void updateMarket(Market updMarket)
	{
		for (Market market: markets)
			if (market.getId().equals(updMarket.getId())) {
				market.setBaseCurrency(updMarket.getBaseCurrency());
				market.setQuoteCurrency(updMarket.getQuoteCurrency());
				EmbeddedPojoManager<Market> manager = new EmbeddedPojoManager<Market>(Market.class);
				manager.update(market);
				break;
				
			}
	}
}
