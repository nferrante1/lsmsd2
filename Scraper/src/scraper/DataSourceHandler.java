package scraper;

import scraper.api.SourceConnector;
import scraper.entities.DataSource;

public class DataSourceHandler extends Thread {
	private final DataSource dataSource;
	private final SourceConnector connector;
	public DataSourceHandler(DataSource dataSource, SourceConnector connector)
	{
		super();
		this.connector = connector;
		this.dataSource = dataSource;
	}
	
	public void run() {}
};
