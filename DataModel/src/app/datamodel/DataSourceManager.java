package app.datamodel;

import java.lang.reflect.Field;
import java.util.HashMap;

import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.Market;

public final class DataSourceManager extends StorablePojoManager<DataSource>
{
	public DataSourceManager()
	{
		super(DataSource.class);
	}

	@Override
	protected void delete(DataSource source)
	{
		MarketDataManager marketDataManager = new MarketDataManager();
		marketDataManager.delete(source.getName());
		super.delete(source);
	}

	@Override
	protected void update(DataSource source)
	{
		MarketDataManager marketDataManager = new MarketDataManager();
		for (Market market : source.getMarkets()) {
			if (market.isDeleting()) {
				marketDataManager.delete(source.getName(), market.getId());
				continue;
			}
			if (!market.isStaged())
				continue;
			HashMap<String, Object> updatedFields = market.getUpdatedFields();
			if (!updatedFields.containsKey("granularity"))
				continue;
			int oldGranularity = (int)updatedFields.get("granularity");
			Field granularityField;
			try {
				granularityField = market.getClass().getDeclaredField("granularity");
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
				continue;
			}
			granularityField.setAccessible(true);
			int newGranularity;
			try {
				newGranularity = granularityField.getInt(market);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
			if (newGranularity < oldGranularity || newGranularity % oldGranularity != 0)
				marketDataManager.delete(source.getName(), market.getId());
		}
		super.update(source);
	}
}
