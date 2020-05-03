package app.scraper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import app.datamodel.DataSourceManager;
import app.datamodel.StorablePojoCursor;

import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.DataSource;
import app.scraper.net.BinanceConnector;
import app.scraper.net.CoinbaseConnector;
import app.scraper.net.SourceConnector;

public final class Scraper
{
	
	private static Map<String, Class<? extends SourceConnector>> sourceConnectorMap = Map.ofEntries(
			Map.entry("COINBASE", CoinbaseConnector.class),
			Map.entry("BINANCE", BinanceConnector.class)
		);
	private static List<Worker> workers = new ArrayList<Worker>();
	private static int stopCount = 1;

	public static void main(String[] args)
	{
		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			parseOptions(cmd, options);
		} catch (ParseException ex) {
			Logger.getLogger(Scraper.class.getName()).warning("Can not parse command line options: " + ex.getMessage());
		}
		
		setupDBManager();

		start();
		listenForSync();
		System.err.println("Unknown error. Stopping...");
		stop();
	}

	private static void start() {
		stopCount--;
		if(stopCount >= 1)
			return;
		System.out.println("Starting Workers...");
		createWorkers();
		for (Worker worker: workers)
			worker.start();
	}
	
	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "print this message."));
		Option logLevelOpt = new Option("l", "log-level", true, "set log level.");
		logLevelOpt.setType(Level.class);
		logLevelOpt.setArgName("LEVEL");
		options.addOption(logLevelOpt);
		Option serverAddress = (new Option("H", "host", true, "Server host name or ip address"));
		serverAddress.setType(String.class);
		serverAddress.setArgName("HOST");
		options.addOption(serverAddress);
		Option serverPort = (new Option("p", "port", true, "Server port"));
		serverPort.setType(Integer.class);
		serverPort.setArgName("PORT");
		Option standalone = new Option("s", "standalone", false, "Disable MongoDB sharding");
		options.addOption(standalone);
		return options;
	}

	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("app [-h | --help] [-l <LEVEL> | --log-level <LEVEL>] [-H <HOST> | --host <HOST>] [-p <PORT> | --port <PORT>]",
				"", options, "\nLOG LEVELS:\n" +
				"ALL: print all logs.\n" +
				"FINEST: print all tracing logs.\n" +
				"FINER: print most tracing logs.\n" +
				"FINE: print some tracing logs.\n" +
				"CONFIG: print all config logs.\n" +
				"INFO: print all informational logs.\n" +
				"WARNING: print all warnings and errors. (default)\n" +
				"SEVERE: print only errors.\n" +
				"OFF: disable all logs." 
			);
			System.exit(0);
		}
		if(cmd.hasOption("standalone"))
		{
			DBManager.setStandalone(true);
		}
		
	}

	private static void stop() 
	{
		stopCount++;
		if(stopCount > 1)
			return;
		System.out.println("Stopping workers...");
		for (Worker worker: workers)
			worker.interrupt();
		for (Worker worker: workers)
			while (worker.isAlive())
				try {
					worker.join();
				} catch (InterruptedException e) {
				}
		workers.clear();
		System.out.println("All threads stopped.");		
	}
	
	private static void listenForSync()
	{
		ServerSocket listeningSocket = null;
		try {
			listeningSocket = new ServerSocket(5656);
			while (true) {
				Socket socket = listeningSocket.accept();
				InputStream is = socket.getInputStream();
				DataInputStream dis = new DataInputStream(is);

				String msg = dis.readUTF();
				if(msg.equals("START")) {
					start();
				}
				else if (msg.equals("STOP")) {
					stop();
				}

				

				OutputStream os = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				dos.writeUTF("ACK");
				dos.flush();

				dos.close();
				dis.close();
				socket.close();
				
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			if (listeningSocket != null);
				try {
					listeningSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private static void setupDBManager()
	{
	}

	private static void createWorkers()
	{
		DataSourceManager manager = new DataSourceManager();
		StorablePojoCursor<DataSource> cursor = (StorablePojoCursor<DataSource>)manager.find();
		List<DataSource> sources = cursor.toList();
		cursor.close();

		for (Map.Entry<String, Class<? extends SourceConnector>> sourceConnector: sourceConnectorMap.entrySet())
		{
			DataSource source = null;
			for (DataSource curSource: sources)
				if (curSource.getName().equals(sourceConnector.getKey()))
					source = curSource;
			if (source == null)
				source = new DataSource(sourceConnector.getKey());
			SourceConnector connector;
			try {
				connector = sourceConnector.getValue().getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
			workers.add(new Worker(source, connector));
		}
	}

}
