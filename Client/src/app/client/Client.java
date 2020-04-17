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

import app.client.config.Configuration;
import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.LoginMenu;
import app.common.net.ResponseMessage;
import app.common.net.entities.Entity;
import app.common.net.entities.MarketInfo;

public class Client
{
	public static void main(String[] args)
	{
		ResponseMessage message= Protocol.getInstance().performLogin("user", "pass");
		
		message = Protocol.getInstance().browseMarkets("ETH", 1);
		
		for(Entity m : message.getEntities())
			System.out.println(((MarketInfo) m).getId());
//		Logger.getLogger(Client.class.getName()).entering(Client.class.getName(), "main", args);
//
//		Options options = createOptions();
//		CommandLineParser parser = new DefaultParser();
//		CommandLine cmd = null;
//		try {
//			cmd = parser.parse(options, args);
//			parseOptions(cmd, options);
//		} catch (ParseException ex) {
//			Logger.getLogger(Client.class.getName()).warning("Can not parse command line options: " + ex.getMessage());
//		}
//
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//		Configuration config = Configuration.getConfig();
//
//		if (cmd == null || !cmd.hasOption("log-level"))
//			setLogLevel(config.getLogLevel());
//
//		launchCLI(args);
	}

	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "print this message."));
		Option logLevelOpt = new Option("l", "log-level", true, "set log level.");
		logLevelOpt.setType(Level.class);
		logLevelOpt.setArgName("LEVEL");
		options.addOption(logLevelOpt);

		return options;
	}

	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("app [-h | --help] [-l <LEVEL> | --log-level <LEVEL>]",
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
