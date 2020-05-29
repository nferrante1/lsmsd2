import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

	@StrategyParameter("Output file name [*.txt,*.csv,*.dat,*.json]")
	private String fileName;
	@StrategyParameter("Indicator name (eg. SMAu14)")
	private String indicatorName;
	@StrategyParameter("Print header?")
	private boolean printHeader;

	private PrintWriter writer;
	private Indicator indicator;

	@Override
	public String name()
	{
		return "Write File Strategy (for debugging) [randId: " + (new Random()).nextInt(Integer.MAX_VALUE) + "]";
	}

	@Override
	public boolean validate()
	{
		fileName = fileName.replace("/", File.separator);
		return (fileName.endsWith(".txt") || fileName.endsWith(".csv")
			|| fileName.endsWith(".dat") || fileName.endsWith(".json"))
			&& indicatorName.matches("^(SMA|EMA|MOM|StdDev)(o|h|l|c|v|u|d|tp|tr)?([1-9][0-9]*)?$");
	}

	@Override
	public List<Indicator> indicators()
	{
		List<Indicator> indicators = new ArrayList<Indicator>();
		Pattern regex = Pattern.compile("^(SMA|EMA|MOM|StdDev)(o|h|l|c|v|u|d|tp|tr)?([1-9][0-9]*)?$");
		Matcher matcher = regex.matcher(indicatorName);
		matcher.matches();
		String name = matcher.group(1);
		String ipName = null;
		String period = null;
		try {
			ipName = matcher.group(2);
			period = matcher.group(3);
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
		} else {
			throw new RuntimeException("Invalid indicator: " + indicatorName + ".");
		}

		indicators.add(indicator);
		return indicators;
	}

	@Override
	public void init(Journal journal)
	{
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
		if (printHeader)
			printHeader();
	}

	private double getIndicatorValue()
	{
		if (SMA.class.isAssignableFrom(indicator.getClass()))
			return ((SMA)indicator).value();
		if (EMA.class.isAssignableFrom(indicator.getClass()))
			return ((EMA)indicator).value();
		if (MOM.class.isAssignableFrom(indicator.getClass()))
			return ((MOM)indicator).value();
		if (StdDev.class.isAssignableFrom(indicator.getClass()))
			return ((StdDev)indicator).value();
		return Double.NaN;
	}

	private void printHeader()
	{
		writer.println("Strategy: Write File Strategy");
		writer.println("Market: " + market + (inverseCross ? " (inverted)" : ""));
		writer.println("Granularity: " + granularity + " minutes");
		writer.println("Start time: " + startTime);
		writer.println("End time: " + endTime);
		writer.println("Indicator: " + indicatorName);
		if (fileName.endsWith(".csv"))
			writer.println("t,o,h,l,c,v," + indicatorName);
		else if (fileName.endsWith(".dat"))
			writer.println("t\to\th\tl\tc\tv\t" + indicatorName);
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
			Document doc = new Document("time", candle.getTime().getEpochSecond())
				.append("open", candle.getOpen())
				.append("high", candle.getHigh())
				.append("low", candle.getLow())
				.append("close", candle.getClose())
				.append("volume", candle.getVolume())
				.append(indicatorName, indValue);
			writer.println(doc.toJson());
		} else {
			throw new RuntimeException("Unrecognized output file format (allowed: txt,csv,dat,json).");
		}
	}

	@Override
	public void process(Journal journal, Candle candle)
	{
		print(candle, getIndicatorValue());
	}

	@Override
	public void finish(Journal journal)
	{
		writer.close();
	}
}
