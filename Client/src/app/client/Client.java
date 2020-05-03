package app.client;

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

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.LoginMenu;

public final class Client
{
	public static void main(String[] args)
	{
		Logger.getLogger(Client.class.getName()).entering(Client.class.getName(), "main", args);

		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			parseOptions(cmd, options);
		} catch (ParseException ex) {
			Logger.getLogger(Client.class.getName()).warning("Can not parse command line options: " + ex.getMessage());
		}
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		launchCLI(args);
	}

	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "print this message."));
		Option logLevelOpt = new Option("l", "log-level", true, "set log level.");
		logLevelOpt.setType(Level.class);
		logLevelOpt.setArgName("LEVEL");
		options.addOption(logLevelOpt);
		Option serverAddress = new Option("H", "host", true, "Server host name or ip address");
		serverAddress.setType(String.class);
		serverAddress.setArgName("HOST");
		options.addOption(serverAddress);
		Option serverPort = new Option("p", "port", true, "Server port");
		serverPort.setType(Integer.class);
		serverPort.setArgName("PORT");
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
			close();
		}
		if (cmd.hasOption("log-level")) {
			String logLevelName = cmd.getOptionValue("log-level").toUpperCase();
			Level logLevel;
			try {
				logLevel = Level.parse(logLevelName);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(Client.class.getName()).warning("Invalid log level specified (" + logLevelName + "). Using default: WARNING.");
				logLevel = Level.WARNING;
			}
			setLogLevel(logLevel);
		}
		if(cmd.hasOption("host")) {
			String host = cmd.getOptionValue("host");
			if(!host.isBlank())
				Protocol.getInstance().setServerAddress(host);
		}
		if(cmd.hasOption("port")) {
			int port = Integer.parseInt(cmd.getOptionValue("port"));
			if(port <= 0 || port > 65535) {
				
			}else {
				Protocol.getInstance().setServerPort(port);
			}
		}
		launchCLI(cmd.getArgs());
	}

	private static void launchCLI(String[] args)
	{
		Logger.getLogger(Client.class.getName()).entering(Client.class.getName(), "launchCLI", args);
		Console.println("WELCOME TO CLIENT!");
		new LoginMenu().show();

		Logger.getLogger(Client.class.getName()).exiting(Client.class.getName(), "launchCLI", args);
		close();
	}

	private static void close()
	{
		Logger.getLogger(Client.class.getName()).fine("Exiting...");
		Console.close();
		System.exit(0);
	}

	private static void setLogLevel(Level level)
	{
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(level);
		for (Handler handler: rootLogger.getHandlers())
			handler.setLevel(level);

		Logger.getLogger(Client.class.getName()).config("Log level set to " + level + ".");
	}
}
