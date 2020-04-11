package app.common.net.entities;

import java.time.Instant;

public class Report {
	String id;
	String marketName;
	Instant start;
	Instant end;
	String author;
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getMarketName()
	{
		return marketName;
	}
	public void setMarketName(String marketName)
	{
		this.marketName = marketName;
	}
	public Instant getStart()
	{
		return start;
	}
	public void setStart(Instant start)
	{
		this.start = start;
	}
	public Instant getEnd()
	{
		return end;
	}
	public void setEnd(Instant end)
	{
		this.end = end;
	}
	public String getAuthor()
	{
		return author;
	}
	public void setAuthor(String author)
	{
		this.author = author;
	}
}
