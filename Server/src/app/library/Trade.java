package app.library;

import java.time.Instant;

public final class Trade
{
	private Instant entryTime;
	private double amount;
	private double entryValue;
	private double exitValue;
	private Instant exitTime;
	private long duration;

	Trade(Instant entryTime, double amount, double value)
	{
		this.entryTime = entryTime;
		this.amount = amount;
		entryValue = value;
	}

	void close(Instant time, double value, int granularity)
	{
		exitTime = time;
		exitValue = value;
		duration = (exitTime.getEpochSecond() - entryTime.getEpochSecond()) / (granularity * 60);
	}

	public boolean open()
	{
		return exitTime == null;
	}

	public boolean closed()
	{
		return !open();
	}

	public double amount()
	{
		return amount;
	}

	public Instant entryTime()
	{
		return entryTime;
	}

	public Instant exitTime()
	{
		return exitTime;
	}

	public double entryValue()
	{
		return entryValue;
	}

	public double exitValue()
	{
		return exitValue;
	}

	public double profit()
	{
		if (open())
			return 0.0;
		return ((exitValue - entryValue) / entryValue) * amount;
	}

	public long duration()
	{
		if (open())
			return -1L;
		return duration;
	}

	public boolean profitable()
	{
		return profit() > 0.0;
	}
}
