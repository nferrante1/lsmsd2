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
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.mongodb.WriteConcern;

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
	private static int listeningPort = 5656;

	public static void main(String[] args)
	{
		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			parseOptions(cmd, options);
		} catch (ParseException ex) {
			Logger.getLogger(Scraper.class.getName())
				.warning("Can not parse command line options: " + ex.getMessage());
		}

		DBManager.setWriteConcern(WriteConcern.W1);

		start();
		listenForSync();
		Logger.getLogger(Scraper.class.getName()).severe("Unknown error. Stopping...");
		stop();
	}

	private static void start()
	{
		stopCount--;
		if (stopCount >= 1)
			return;
		Logger.getLogger(Scraper.class.getName()).info("Starting Workers...");
		createWorkers();
		for (Worker worker : workers)
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
		Option connectionString = new Option("c", "connection-string", true, "Set MongoDB connection string.");
		connectionString.setType(String.class);
		connectionString.setArgName("CONNSTR");
		options.addOption(connectionString);
		Option dbName = new Option("d", "dbname", true, "Set MongoDB database name.");
		dbName.setArgName("DBNAME");
		dbName.setType(String.class);
		options.addOption(dbName);
		Option serverPort = (new Option("p", "port", true, "Listening port."));
		serverPort.setType(Integer.class);
		serverPort.setArgName("PORT");
		Option standalone = new Option("s", "standalone", false, "Disable MongoDB sharding.");
		options.addOption(standalone);
		return options;
	}

	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(
				"app [-h | --help] [-c <CONNSTR> | --connection-string <CONNSTR>] [-d <DBNAME> | --dbname <DBNAME>] [-s | --standalone] [-p <PORT> | --port <PORT>] [-l <LEVEL> | --log-level <LEVEL>]",
				"", options,
				"\nLOG LEVELS:\n" + "ALL: print all logs.\n" + "FINEST: print all tracing logs.\n" +
					"FINER: print most tracing logs.\n" + "FINE: print some tracing logs.\n" +
					"CONFIG: print all config logs.\n" + "INFO: print all informational logs.\n" +
					"WARNING: print all warnings and errors. (default)\n" +
					"SEVERE: print only errors.\n" + "OFF: disable all logs.");
			System.exit(0);
		}
		if (cmd.hasOption("log-level")) {
			String logLevelName = cmd.getOptionValue("log-level").toUpperCase();
			Level logLevel;
			try {
				logLevel = Level.parse(logLevelName);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(Scraper.class.getName()).warning("Invalid log level specified (" + logLevelName + "). Using default: WARNING.");
				logLevel = Level.WARNING;
			}
			setLogLevel(logLevel);
		}
		if(cmd.hasOption("connection-string")) {
			String conn = cmd.getOptionValue("connection-string");
			if(!conn.isBlank())
				DBManager.setConnectionString(conn);
		}
		if(cmd.hasOption("dbname")) {
			String name = cmd.getOptionValue("dbname");
			if(!name.isBlank())
				DBManager.setDatabaseName(name);
		}
		if (cmd.hasOption("port")) {
			try {
				listeningPort = Integer.parseInt(cmd.getOptionValue("port", "5656"));
				if (listeningPort < 0 || listeningPort > 65535) {
					NumberFormatException ex = new NumberFormatException("The port must be a number between 0 and 65535.");
					Logger.getLogger(Scraper.class.getName()).throwing(Scraper.class.getName(), "parseOptions", ex);
					throw ex;
				}
			} catch (NumberFormatException ex) {
				Logger.getLogger(Scraper.class.getName()).warning("Invalid port specified. Using default: 5656.");
				listeningPort = 5656;
			}
		} else {
			Logger.getLogger(Scraper.class.getName()).config("Using default port 5656.");
			listeningPort = 5656;
		}
		if (cmd.hasOption("standalone"))
			DBManager.setStandalone(true);
	}

	private static void stop()
	{
		stopCount++;
		if (stopCount > 1)
			return;
		Logger.getLogger(Scraper.class.getName()).info("Stopping workers...");
		for (Worker worker : workers)
			worker.interrupt();
		for (Worker worker : workers)
			while (worker.isAlive())
				try {
					worker.join();
				} catch (InterruptedException e) {
				}
		workers.clear();
		Logger.getLogger(Scraper.class.getName()).info("All workers stopped.");
	}

	private static void listenForSync()
	{
		ServerSocket listeningSocket = null;
		try {
			listeningSocket = new ServerSocket(listeningPort);
			while (true) {
				Socket socket = listeningSocket.accept();
				InputStream is = socket.getInputStream();
				DataInputStream dis = new DataInputStream(is);
				String msg = dis.readUTF();
				if (msg.equals("START"))
					start();
				else if (msg.equals("STOP"))
					stop();
				OutputStream os = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				dos.writeUTF("ACK");
				dos.flush();

				dos.close();
				dis.close();
				socket.close();
			}
		} catch (IOException e) {
			Logger.getLogger(Scraper.class.getName()).severe("Error while reading/writing message from/to server application: " + e.getMessage());
		} finally {
			try {
				if (listeningSocket != null)
					listeningSocket.close();
			} catch (IOException e) {
				Logger.getLogger(Scraper.class.getName()).severe("Error while closing socket with the server application: " + e.getMessage());
			}
		}
	}

	private static void createWorkers()
	{
		DataSourceManager manager = new DataSourceManager();
		StorablePojoCursor<DataSource> cursor = (StorablePojoCursor<DataSource>)manager.find();
		List<DataSource> sources = cursor.toList();
		cursor.close();

		for (Map.Entry<String, Class<? extends SourceConnector>> sourceConnector: sourceConnectorMap.entrySet()) {
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
				Logger.getLogger(Scraper.class.getName()).throwing(Scraper.class.getName(), "createWorkers", e);
				throw new RuntimeException(e);
			}
			workers.add(new Worker(source, connector));
		}
	}

	private static void setLogLevel(Level level)
	{
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(level);
		for (Handler handler: rootLogger.getHandlers())
			handler.setLevel(level);

		Logger.getLogger(Scraper.class.getName()).config("Log level set to " + level + ".");
	}
}
