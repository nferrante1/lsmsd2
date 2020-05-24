import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import app.library.annotations.*;
import app.library.indicators.enums.*;
import app.library.indicators.*;
import app.library.*;

public class WriteFileStrategy implements ExecutableStrategy
{
	@StrategyParameter
	private String market;
	@StrategyParameter
	private boolean inverseCross;
	@StrategyParameter
	private int granularity;
	@StrategyParameter
	private Instant startTime;
	@StrategyParameter
	private Instant endTime;

	@StrategyParameter("Output file name (*.txt,*.csv,*.dat,*.json)")
	private String fileName;
	@StrategyParameter("Indicator name (eg. SMAu14)")
	private String indicatorName;
	@StrategyParameter("Skip NaNs?")
	private boolean skipNaN;

	private PrintWriter writer;

	private Indicator indicator;

	public WriteFileStrategy()
	{
	}

	@Override
	public String getName()
	{
		return "Write File Strategy (for debugging)";
	}

	@Override
	public List<Indicator> indicators() // FIXME: MOM9 not working
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		Pattern regex = Pattern.compile("^(SMA|EMA|MOM|StdDev)([ud(tp)(tr)])?([1-9][0-9]*)?$");
		Matcher matcher = regex.matcher(indicatorName);
		matcher.matches();
		String name = matcher.group(0);
		String ipName = null;
		String period = null;
		try {
			ipName = matcher.group(1);
			period = matcher.group(2);
		} catch(IndexOutOfBoundsException e) {
		}
		InputPrice ip = InputPrice.fromShortName(ipName);
			if (ip == null)
				ip = InputPrice.CLOSE;
		if (name.equalsIgnoreCase("SMA")) {
			if (period == null || period.isEmpty())
				indicator = new SMA(ip);
			else
				indicator = new SMA(Integer.parseInt(period), ip);
		} else if (name.equalsIgnoreCase("EMA")) {
			if (period == null || period.isEmpty())
				indicator = new EMA();
			else
				indicator = new EMA(Integer.parseInt(period));
		} else if (name.equalsIgnoreCase("MOM")) {
			if (period == null || period.isEmpty())
				indicator = new MOM();
			else
				indicator = new MOM(Integer.parseInt(period));
		} else if (name.equalsIgnoreCase("StdDev")) {
			if (period == null || period.isEmpty())
				indicator = new StdDev(ip);
			else
				indicator = new StdDev(Integer.parseInt(period), ip);
		}

		indicators.add(indicator);
		return indicators;
	}

	@Override
	public boolean validate()
	{
		fileName = fileName.replace("/", File.separator);
		return (fileName.endsWith(".txt") || fileName.endsWith(".csv")
			|| fileName.endsWith(".dat") || fileName.endsWith(".json"))
			&& indicatorName.matches("^(SMA[ud(tp)(tr)]*|EMA|MOM|StdDev[(tp)(tr)]*)([1-9][0-9]*)?$");
	}

	@Override
	public void init(Journal journal)
	{
		System.out.println("Using indicator " + indicator.getClass().getName() + " with period " + getIndicatorPeriod() + " on " + getIndicatorInputPrice() + ".");
		if (fileName.contains(File.separator)) {
			String dirName = fileName.substring(0, fileName.lastIndexOf(File.separator));
			File dir = new File(dirName);
			dir.mkdirs();
		}
		try {
			writer = new PrintWriter(new FileOutputStream(fileName));
		} catch (IOException e) {
			throw new RuntimeException("Can not write file '" + fileName + "'.", e);
		}
	}

	private int getIndicatorPeriod()
	{
		if (SMA.class.isAssignableFrom(indicator.getClass()))
			return ((SMA)indicator).getPeriod();
		if (EMA.class.isAssignableFrom(indicator.getClass()))
			return ((EMA)indicator).getPeriod();
		if (MOM.class.isAssignableFrom(indicator.getClass()))
			return ((MOM)indicator).getPeriod();
		if (StdDev.class.isAssignableFrom(indicator.getClass()))
			return ((StdDev)indicator).getPeriod();
		return -1;
	}

	private String getIndicatorInputPrice()
	{
		if (SMA.class.isAssignableFrom(indicator.getClass()))
			return ((SMA)indicator).getInputPrice().name();
		if (StdDev.class.isAssignableFrom(indicator.getClass()))
			return ((StdDev)indicator).getInputPrice().name();
		return InputPrice.CLOSE.name();
	}

	private double getIndicatorValue()
	{
		if (SMA.class.isAssignableFrom(indicator.getClass()))
			return ((SMA)indicator).getValue();
		if (EMA.class.isAssignableFrom(indicator.getClass()))
			return ((EMA)indicator).getValue();
		if (MOM.class.isAssignableFrom(indicator.getClass()))
			return ((MOM)indicator).getValue();
		if (StdDev.class.isAssignableFrom(indicator.getClass()))
			return ((StdDev)indicator).getValue();
		return Double.NaN;
	}

	private void print(Candle candle, double indValue)
	{
		if (fileName.endsWith(".txt")) {
			writer.println("T: " + candle.getTime() + " | "
				+ "O: " + candle.getOpen() + " | "
				+ "H: " + candle.getHigh() + " | "
				+ "L: " + candle.getLow() + " | "
				+ "C: " + candle.getClose() + " | "
				+ "V: " + candle.getVolume() + " | "
				+ indicatorName + ": " + indValue);
		} else if (fileName.endsWith(".csv")) {
			writer.println(candle.getTime().getEpochSecond() + ","
				+ candle.getOpen() + "," + candle.getHigh() + ","
				+ candle.getLow() + "," + candle.getClose() + ","
				+ candle.getVolume() + "," + indValue);
		} else if (fileName.endsWith(".dat")) {
			writer.println(candle.getTime().getEpochSecond() + "\t"
				+ candle.getOpen() + "\t" + candle.getHigh() + "\t"
				+ candle.getLow() + "\t" + candle.getClose() + "\t"
				+ candle.getVolume() + "\t" + indValue);
		} else if (fileName.endsWith(".json")) {
			Document doc = new Document("time", candle.getTime().getEpochSecond());
			doc.append("open", candle.getOpen());
			doc.append("high", candle.getHigh());
			doc.append("low", candle.getLow());
			doc.append("close", candle.getClose());
			doc.append("volume", candle.getVolume());
			doc.append(indicatorName, indValue);
			writer.println(doc.toJson());
		} else {
			writer.println("Unrecognized file format.");
		}
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		double indValue = getIndicatorValue();
		if (skipNaN && Double.isNaN(indValue))
			return;
		print(candle, indValue);
	}

	@Override
	public void finish(Journal journal)
	{
		writer.close();
	}
}
