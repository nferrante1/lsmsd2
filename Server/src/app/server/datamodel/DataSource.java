package app.server.datamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import app.server.datamodel.mongo.CollectionName;
import app.server.datamodel.mongo.DataObject;
import app.server.datamodel.mongo.DataObjectId;

@CollectionName("Sources")
public class DataSource extends DataObject
{
	@DataObjectId
	@SerializedName(value = "_id")
	protected String name;
	protected boolean enabled;
	protected List<Market> markets = new ArrayList<Market>();
	private List<Market> newMarkets = new ArrayList<Market>();
	
	private DataSource()
	{
		super();
	}
	public DataSource(String name)
	{
		super();
		this.name = name;
		enabled = true;
	}
	
	public static List<DataSource> load()
	{
		return load(DataSource.class);
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
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
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
		newMarkets.add(market);
	}
	
	public void removeMarket(String marketId)
	{
		Iterator<Market> marketsIterator = markets.iterator();
		while (marketsIterator.hasNext()) {
			Market market = marketsIterator.next();
			if (market.getId().equals(marketId)) {
				marketsIterator.remove();
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
				break;
			}
	}
	
	@Override
	public void save()
	{
		if (!newMarkets.isEmpty()) {
			if (isSaved())
				for (Market market: newMarkets)
					market.save();
			markets.addAll(newMarkets);
			newMarkets.clear();
		}
		super.save();
	}
}
