package app.datamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;

import com.google.gson.annotations.SerializedName;

import app.datamodel.mongo.CollectionName;
import app.datamodel.mongo.DataObject;
import app.datamodel.mongo.DataObjectId;
import app.datamodel.mongo.Pojo;
import app.datamodel.mongo.PojoManager;

@CollectionName("Sources")
public class DataSource extends Pojo
{
	@BsonId
	protected String name;
	protected boolean enabled;
	protected List<Market> markets = new ArrayList<Market>();
	private transient List<Market> newMarkets = new ArrayList<Market>();
	private static transient PojoManager<DataSource> manager;
	
	private DataSource()
	{
		super();
		manager = new PojoManager<DataSource>(DataSource.class);
	}
	public DataSource(String name)
	{
		super();
		this.name = name;
		enabled = true;
	}
	
	public static List<DataSource> load()
	{
		return manager.find();
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
	

	public void save()
	{
		if (!newMarkets.isEmpty()) {
			if (isSaved())
				for (Market market: newMarkets)
					market.getManager().save();
			markets.addAll(newMarkets);
			newMarkets.clear();
		}
		getManager().save();
	}

	protected PojoManager<DataSource> getManager()
	{
		if (manager == null)
			manager = new PojoManager<DataSource>(DataSource.class);
		return manager;
	}
}
