package app.server;



import java.io.IOException;
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

import app.datamodel.mongo.DBManager;


public class Server {
	
	
	
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

		startServer();
		Logger.getLogger(Server.class.getName()).exiting(Server.class.getName(), "main", args);
	}

	private static void startServer()
	{
		Logger.getLogger(Server.class.getName()).entering(Server.class.getName(), "startServer");
		ClientPool pool = null;
		try {
			Logger.getLogger(Server.class.getName()).info("Starting server...");
			pool = new ClientPool(port);
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
		Option hostOpt = new Option("H", "host", true, "Specify MongoDB database hostname (default: localhost).");
		hostOpt.setType(String.class);
		hostOpt.setArgName("HOST");
		options.addOption(hostOpt);
		Option dbNameOpt = new Option("d", "dbname", true, "Specify MongoDB database name (default: admin).");
		dbNameOpt.setType(String.class);
		dbNameOpt.setArgName("DBNAME");
		options.addOption(dbNameOpt);
		Option dbportOpt = new Option("dbport", true, "Specify MongoDB database port (default: 27017).");
		hostOpt.setType(Integer.class);
		hostOpt.setArgName("PORT");
		options.addOption(dbportOpt);
		Option userOpt = new Option("u", "user", true, "Specify MongoDB database username (default: root).");
		userOpt.setType(String.class);
		userOpt.setArgName("USER");
		options.addOption(userOpt);
		Option passOpt = new Option("p", "pass", true, "Specify MongoDB database password (default: <empty>).");
		passOpt.setType(String.class);
		passOpt.setArgName("PASS");
		options.addOption(passOpt);
		Option portOpt = new Option("P", "port", true, "Set listening port (default: 8888).");
		portOpt.setType(Integer.class);
		portOpt.setArgName("PORT");
		options.addOption(portOpt);
		Option logLevelOpt = new Option("l", "log-level", true, "Set log level.");
		logLevelOpt.setType(Level.class);
		logLevelOpt.setArgName("LEVEL");
		options.addOption(logLevelOpt);

		return options;
	}

	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Server [-h | --help] [-H <HOST> | --host <HOST>] [-d <DBNAME> | --dbname <DBNAME>] [--dbport <PORT>] [-u <USER> | --user <USER>] [-p <PASS> | --pass <PASS>] [-P <PORT> | --port <PORT>] [-l <LEVEL> | --log-level <LEVEL>]",
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

		if (cmd.hasOption("host")) {
			String host = cmd.getOptionValue("host");
			if (!host.isBlank()) {
				DBManager.setHostname(host);
			}
		}
		if(cmd.hasOption("dbname")) {
			String dbname = cmd.getOptionValue("dbname");
			if (!dbname.isBlank()) {
				DBManager.setDatabase(dbname);
			}
		}
		if (cmd.hasOption("dbport")) {
			String dbport = cmd.getOptionValue("dbport");
			try {
				int intdbport = Integer.parseInt(dbport);
				if (intdbport < 0 || intdbport > 65535) {
					NumberFormatException ex = new NumberFormatException("The dbport must be a number between 0 and 65535.");
					Logger.getLogger(Server.class.getName()).throwing(Server.class.getName(), "parseOptions", ex);
					throw ex;
				}
				DBManager.setPort(intdbport);
			} catch (NumberFormatException ex) {
	
			}
		}

		if (cmd.hasOption("user")) {
			String user = cmd.getOptionValue("user");
			if (!user.isBlank())
				DBManager.setUsername(user);
			
		}
		if (cmd.hasOption("pass")) {
			String pass = cmd.getOptionValue("pass");
			DBManager.setPassword(pass);
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
		DBManager.setHostname("127.0.0.1");
		DBManager.setPort(27017);
		 DBManager.setUsername("root");
		DBManager.setPassword("rootpass");
		DBManager.setDatabase("mydb");
	}
	

}
