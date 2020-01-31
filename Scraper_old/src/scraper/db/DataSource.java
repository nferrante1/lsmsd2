package scraper.db;

import java.util.ArrayList;
import java.util.Iterator;

import scraper.sources.SourceConnector;

public class DataSource implements Runnable
{
	protected String name;
	protected boolean enabled;
	protected boolean streamingEnabled;
	protected ArrayList<Market> markets;
	private Iterator<Market> marketsIterator;
	private SourceConnector connector;
	
	private DataSource()
	{
		
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	protected void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	protected void setStreamingEnabled(boolean enabled)
	{
		this.streamingEnabled = enabled;
	}
	
	public void setConnector(SourceConnector connector)
	{
		this.connector = connector;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public boolean isStreamingEnabled()
	{
		return streamingEnabled;
	}
	
	public SourceConnector getConnector()
	{
		return connector;
	}
	
	protected void updateMarkets()
	{
		
	}
	
	@Override
	public void run()
	{
		
	}
}
