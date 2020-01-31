package scraper.entities;

import java.util.ArrayList;

public class DataSource {
	protected String name;
	protected boolean enabled;
	protected boolean streamingEnabled;
	protected ArrayList<Market> markets;
	
	private DataSource() {}
	
	public DataSource(String name) 
	{
		this.name = name;
	}
	
	public static ArrayList<DataSource> loadSources(){ return null;}
	
	public void mergeMarkets(Market... markets) {}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	protected void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}
	
	protected void setStreaming(boolean streaming) 
	{
		this.streamingEnabled = streaming;
	}
	
	public String getName() 
	{
		return this.name;
	}
	
	public boolean isEnabled() 
	{
		return this.enabled;
	}
	
	public boolean isStreamingEnabled() 
	{
		return this.enabled && this.streamingEnabled;
	}
	
	public ArrayList<Market> getMarkets()
	{
		return this.markets;
	}
}
