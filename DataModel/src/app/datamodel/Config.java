package app.datamodel;

import java.time.Instant;
import java.util.HashMap;

import app.datamodel.mongo.EmbeddedPojo;
import app.datamodel.mongo.EmbeddedPojoManager;

public class Config extends EmbeddedPojo {
	protected String market;
	protected boolean inverseCross;
	protected int granularity;
	protected Instant startTime;
	protected Instant endTime;
	protected HashMap<String, Object> parameters = new HashMap<String, Object>();
	private static transient EmbeddedPojoManager<Config> manager;
	
	public Config(String market, boolean inverse, int granularity, Instant startTime, Instant endTime)
	{
		this.market = market;
		this.inverseCross = inverse;
		this.granularity = granularity;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public static EmbeddedPojoManager<Config> getManager()
	{
		if(manager == null) 
			manager = new EmbeddedPojoManager<Config>(Config.class);
		return manager;
	}
	
	public String getMarket()
	{
		return market;
	}

	public boolean isInverseCross()
	{
		return inverseCross;
	}

	public int getGranularity()
	{
		return granularity;
	}

	public Instant getStartTime()
	{
		return startTime;
	}

	public Instant getEndTime()
	{
		return endTime;
	}
	
	public HashMap<String, Object> getParameters()
	{
		return parameters;
	}
	
	public void setParameter(String name, Object value)
	{
		parameters.put(name, value);
	}
	
	public Object getParameter(String name)
	{
		return parameters.get(name);
	}
}
