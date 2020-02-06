package app.scraper;

import app.scraper.data.DataSource;
import app.scraper.net.SourceConnector;

final class Worker extends Thread
{
	private final DataSource source;
	private final SourceConnector connector;
	
	public Worker(DataSource source, SourceConnector connector)
	{
		this.source = source;
		this.connector = connector;
	}
	
	@Override
	public void run()
	{
		System.out.println(getName() + ": source=" + source.getName());
	}
}
