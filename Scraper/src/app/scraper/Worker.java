package app.scraper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import app.datamodel.DataRangeManager;
import app.datamodel.DataSourceManager;
import app.datamodel.MarketDataManager;
import app.datamodel.pojos.Candle;
import app.datamodel.pojos.DataRange;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.Market;
import app.datamodel.pojos.MarketData;
import app.scraper.net.SourceConnector;
import app.scraper.net.data.APICandle;
import app.scraper.net.data.APIMarket;
import app.scraper.net.exceptions.PermanentAPIException;
import app.scraper.net.exceptions.TemporaryAPIException;

final class Worker extends Thread
{
	private final DataSource source;
	private final SourceConnector connector;
	private DataSourceManager sourceManager;

	public Worker(DataSource source, SourceConnector connector)
	{
		this.source = source;
		this.connector = connector;
		sourceManager = new DataSourceManager();
	}

	@Override
	public void run()
	{
		Logger.getLogger(Worker.class.getName() + " - " + getName()).info("source: " + source.getName());
		try {
			execute();
		} catch (InterruptedException e) {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).warning("Interrupted!");
		} catch (Throwable e) {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).severe("Unhandled exception: " + e.getMessage());
		} finally {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).info("Exiting...");
		}
	}

	private void execute() throws InterruptedException
	{
		List<APIMarket> markets = connector.getMarkets();
		if (markets == null || markets.isEmpty()) {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).warning("No markets! Exiting...");
			return;
		}

		for (APIMarket curMarket: markets) {
			Market market = source.getMarket(curMarket.getId());
			if (market == null) {
				source.addMarket(new Market(curMarket.getId(), curMarket.getBaseCurrency(), curMarket.getQuoteCurrency()));
				continue;
			}
			market.setBaseCurrency(curMarket.getBaseCurrency());
			market.setQuoteCurrency(curMarket.getQuoteCurrency());
		}

		List<Market> sourceMarkets = source.getMarkets();

		NextMarket: for (Market sm: sourceMarkets) {
			for (APIMarket m: markets)
				if (sm.getId().equals(m.getId()))
					continue NextMarket;
			sm.delete();
		}

		sourceManager.save(source);

		if (!source.isEnabled()) {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).warning("Source not enabled. Exiting...");
			return;
		}

		updateMarkets();
	}

	private void updateMarkets() throws InterruptedException
	{
		List<Market> sourceMarkets = source.getMarkets();
		int enabledCount = sourceMarkets.size();
		for (Market m: sourceMarkets)
			if (!m.isSyncEnabled())
				enabledCount--;
		if (enabledCount == 0) {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).warning("All markets disabled! Exiting...");
			return;
		}

		while (!interrupted())
			for (Market market: sourceMarkets)
				if (market.isSyncEnabled()) {
					DataRange range = market.getRange();
					if (range != null && range.end != null && range.end
						.isAfter(Instant.now().minusSeconds(market.getGranularity() * 60))) {
						Logger.getLogger(Worker.class.getName() + " - " + getName()).info("Market " + source.getName() + ":" + market.getId() + " is now up-to-date.");
						Thread.sleep(1000);
						continue;
					}
					updateMarket(market);
				}
	}

	private void updateMarket(Market market) throws InterruptedException
	{
		MarketDataManager marketDataManager = new MarketDataManager();
		DataRangeManager dataRangeManager = new DataRangeManager();

		String marketId = market.getId();
		String fullMarketId = source.getName() + ":" + marketId;
		int marketGranularity = market.getGranularity();

		Logger.getLogger(Worker.class.getName() + " - " + getName()).info("Processing " + fullMarketId + "...");

		DataRange range = market.getRange();
		if (range == null) {
			range = dataRangeManager.get(fullMarketId);
			market.setRange(range);
		}
		Instant start = range.end;
		if (start != null)
			start = start.plusSeconds(marketGranularity * 60);

		List<APICandle> sourceCandles;
		try {
			sourceCandles = connector.getCandles(marketId, marketGranularity, start);
		} catch (TemporaryAPIException ex) {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).warning("Temporary error while contacting the source: " + ex.getMessage());
			System.err.println(getName() + ": " + ex.getMessage());
			long millisToWait = ex.getMillisToWait();
			Logger.getLogger(Worker.class.getName() + " - " + getName()).info("Waiting for " + millisToWait + " as requested by source connector.");
			Thread.sleep(millisToWait);
			sourceCandles = null;
		} catch (PermanentAPIException ex) {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).severe("Permanent error while contacting the source: " + ex.getMessage());
			Logger.getLogger(Worker.class.getName() + " - " + getName()).warning("Disabling sync for market " + fullMarketId + " (may resolve the problem).");
			market.setSync(false);
			sourceManager.save(source);
			sourceCandles = null;
		} catch (InterruptedException ex) {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).throwing(Worker.class.getName(), "updateMarket", ex);
			throw ex;
		} catch (Throwable ex) {
			Logger.getLogger(Worker.class.getName() + " - " + getName()).severe("Unknown exception while contacting the source: " + ex.getMessage());
			Logger.getLogger(Worker.class.getName() + " - " + getName()).warning("Waiting for 60 seconds. Hopefully, may resolve the problem.");
			Thread.sleep(60000);
			sourceCandles = null;
		}

		if (sourceCandles == null) // API error
			return;
		if (sourceCandles.isEmpty()) { // Up-to-date
			range.end = Instant.now();
			return;
		}

		int lastCandlesCount = market.getLastCandlesCount();
		if (lastCandlesCount < 0)
			lastCandlesCount = marketDataManager.countLastCandles(fullMarketId);
		int sourceCandlesCount = sourceCandles.size();
		int toUpsert = lastCandlesCount == 0 ? 0 : 1000 - lastCandlesCount;
		toUpsert = Math.min(toUpsert, sourceCandlesCount);
		if (toUpsert > 0) {
			List<Candle> candles = new ArrayList<Candle>(toUpsert);
			for (int i = 0; i < toUpsert; i++)
				candles.add(convertCandle(sourceCandles.get(i)));

			marketDataManager.save(fullMarketId, candles);

			market.setLastCandlesCount(lastCandlesCount + toUpsert);
			if (range.start == null)
				range.start = candles.get(0).getTime();
			range.end = candles.get(candles.size() - 1).getTime();
		}

		List<MarketData> marketDatas = new ArrayList<MarketData>();
		int remainingCandles = sourceCandlesCount - toUpsert;
		int offset = toUpsert;
		while (remainingCandles > 0) {
			List<Candle> candles = new ArrayList<Candle>(Math.min(remainingCandles, 1000));
			int i = 0;
			for (; i < Math.min(remainingCandles, 1000); i++)
				candles.add(convertCandle(sourceCandles.get(offset + i)));
			marketDatas.add(new MarketData(fullMarketId, candles));
			remainingCandles -= i;
			offset += i;
		}
		marketDataManager.save(marketDatas);

		if (!marketDatas.isEmpty()) {
			MarketData lastMarketData = marketDatas.get(marketDatas.size() - 1);
			market.setLastCandlesCount(lastMarketData.getNcandles());
			if (range.start == null)
				range.start = lastMarketData.getStart();
			range.end = lastMarketData.getEnd();
		}
	}

	private Candle convertCandle(APICandle candle)
	{
		return new Candle(candle.getTime(),
			candle.getOpen(),
			candle.getHigh(),
			candle.getLow(),
			candle.getClose(),
			candle.getVolume());
	}
}