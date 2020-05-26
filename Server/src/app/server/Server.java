package app.server;

import java.io.IOException;
import java.util.Scanner;
import java.util.TimeZone;
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

import com.mongodb.ReadPreference;

import app.datamodel.StorablePojoManager;
import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.User;
import app.server.runner.StrategyFile;

public final class Server
{
	private static int port = 8888;

	public static void main(String[] args)
	{
		Logger.getLogger(Server.class.getName()).entering(Server.class.getName(), "main", args);

		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			parseOptions(cmd, options);
		} catch (ParseException ex) {
			Logger.getLogger(Server.class.getName()).warning("Can not parse command line options: " + ex.getMessage());
		}

		setupDBManager();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		createAdmin();

		startServer();
		Logger.getLogger(Server.class.getName()).exiting(Server.class.getName(), "main", args);
	}

	private static void createAdmin()
	{
		StorablePojoManager<User> userManager = new StorablePojoManager<User>(User.class);
		if (userManager.estimatedCount() > 0 || userManager.count() > 0)
			return;
		Scanner scanner = System.console() != null ?
		new Scanner(System.console().reader()) : new Scanner(System.in);
		System.out.println("*** CREATE ADMIN USER ***");
		System.out.println();
		String username = null;
		while (username == null) {
			System.out.print("USERNAME [admin]: ");
			System.out.flush();
			username = scanner.nextLine();
			username.trim();
			if (username.isBlank()) {
				username = "admin";
				break;
			}
			if (!username.matches("^[A-Za-z0-9]{3,32}$")) {
				System.out.println("Invalid username.");
				username = null;
				continue;
			}
		}
		String password = null;
		while (password == null) {
			System.out.print("PASSWORD: ");
			System.out.flush();
			if (System.console() == null)
				password = scanner.nextLine();
			else
				password = new String(System.console().readPassword());
			if (password.isBlank()) {
				System.out.println("Invalid password.");
				password = null;
				continue;
			}
			if (password.length() < 8) {
				System.out.println("Password must be at least 8 chars long.");
				password = null;
				continue;
			}
		}
		User admin = new User(username, password, true);
		userManager.save(admin);
		System.out.println();
		System.out.println("*** ADMIN '" + username + "' CREATED ***");
		System.out.println();
	}

	private static void startServer()
	{
		Logger.getLogger(Server.class.getName()).entering(Server.class.getName(), "startServer");
		RequestHandlerPool pool = null;
		try {
			Logger.getLogger(Server.class.getName()).info("Starting server...");
			pool = new RequestHandlerPool(port);
			Thread thread = new Thread(pool);
			thread.start();
			thread.join();
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			Logger.getLogger(Server.class.getName()).info("Terminating...");
			if (pool != null)
				pool.shutdown();
			Logger.getLogger(Server.class.getName()).exiting(Server.class.getName(), "startServer");
		}
	}

	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "Print this message."));
		Option portOpt = new Option("p", "port", true, "Set listening port (default: 8888).");
		portOpt.setType(Integer.class);
		portOpt.setArgName("PORT");
		options.addOption(portOpt);
		Option logLevelOpt = new Option("l", "log-level", true, "Set log level.");
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
		Option directory = new Option("D", "strategies-dir", true, "Set directory where strategies will be saved.");
		directory.setArgName("DIR");
		directory.setType(String.class);
		options.addOption(directory);
		Option scraperPort = new Option("P", "scraper-port",true,"Set scraper connection port.");
		scraperPort.setType(Integer.class);
		scraperPort.setArgName("PORT");
		options.addOption(scraperPort);
		Option scraperAddress = new Option("H", "scraper-host", true, "Set scraper hostname or ip address.");
		scraperAddress.setType(String.class);
		scraperAddress.setArgName("HOST");
		options.addOption(scraperAddress);
		Option standalone = new Option("s", "standalone", false, "Disable MongoDB sharding.");
		options.addOption(standalone);
		return options;
	}

	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Server [-h | --help] "
					+ "[-d <DBNAME> | --dbname <DBNAME>] "
					+ "[-c <CONNSTR> | --connection-string <CONNSTR>] "
					+ "[-D <DIR> | --strategies-dir <DIR>] "
					+ "[-P <PORT> | --scraper-port <PORT>] "
					+ "[-H <HOST> | --scraper-host <HOST>] "
					+ "[-p <PORT> | --port <PORT>] "
					+ "[-l <LEVEL> | --log-level <LEVEL>]",
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
		if (cmd.hasOption("log-level")) {
			String logLevelName = cmd.getOptionValue("log-level").toUpperCase();
			Level logLevel;
			try {
				logLevel = Level.parse(logLevelName);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(Server.class.getName()).warning("Invalid log level specified (" + logLevelName + "). Using default: WARNING.");
				logLevel = Level.WARNING;
			}
			setLogLevel(logLevel);
		}

		if (cmd.hasOption("port")) {
			try {
				port = Integer.parseInt(cmd.getOptionValue("port", "8888"));
				if (port < 0 || port > 65535) {
					NumberFormatException ex = new NumberFormatException("The port must be a number between 0 and 65535.");
					Logger.getLogger(Server.class.getName()).throwing(Server.class.getName(), "parseOptions", ex);
					throw ex;
				}
			} catch (NumberFormatException ex) {
				Logger.getLogger(Server.class.getName()).warning("Invalid port specified. Using default: 8888.");
				port = 8888;
			}
		} else {
			Logger.getLogger(Server.class.getName()).config("Using default port 8888.");
			port = 8888;
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
		if(cmd.hasOption("scraper-port")) {
			int port = Integer.parseInt(cmd.getOptionValue("scraper-port"));
			if(port <= 0 || port > 65535) {}
			else {
				ScraperController.setPort(port);
			}
		}
		if(cmd.hasOption("scraper-host")) {
			String name = cmd.getOptionValue("scraper-host");
			if(!name.isBlank())
				ScraperController.setAddress(name);
		}
		if(cmd.hasOption("strategies-dir")) {
			String dir = cmd.getOptionValue("strategies-dir");
			if(dir.isBlank())
				StrategyFile.setMainDirectory("strategies");
			else
				StrategyFile.setMainDirectory(dir);
		}
		if(cmd.hasOption("standalone"))
			DBManager.setStandalone(true);
	}

	private static void setLogLevel(Level level)
	{
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(level);
		for (Handler handler: rootLogger.getHandlers())
			handler.setLevel(level);

		Logger.getLogger(Server.class.getName()).config("Log level set to " + level + ".");
	}

	public static void setupDBManager()
	{
		DBManager.setReadPreference(ReadPreference.nearest());
	}
}
