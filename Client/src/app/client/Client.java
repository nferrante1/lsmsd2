package app.client;

import java.util.TimeZone;

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
		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			parseOptions(cmd, options);
		} catch (ParseException ex) {
			System.err.println("WARNING: Can not parse command line options: " + ex.getMessage());
		}
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		launchCLI(args);
	}

	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "print this message."));
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
			formatter.printHelp("app [-h | --help] [-H <HOST> | --host <HOST>] [-p <PORT> | --port <PORT>]", options);
			close();
		}
		if (cmd.hasOption("host")) {
			String host = cmd.getOptionValue("host");
			if (!host.isBlank())
				Protocol.getInstance().setServerAddress(host);
		}
		if (cmd.hasOption("port")) {
			int port;
			try {
				port = Integer.parseInt(cmd.getOptionValue("port", "8888"));
				if (port < 0 || port > 65535) {
					NumberFormatException ex = new NumberFormatException("The port must be a number between 0 and 65535.");
					throw ex;
				}
			} catch (NumberFormatException ex) {
				System.err.println("Invalid port specified. Using default: 8888.");
				port = 8888;
			}
			Protocol.getInstance().setServerPort(port);
		}

		launchCLI(cmd.getArgs());
	}

	private static void launchCLI(String[] args)
	{
		Console.println("WELCOME!");
		new LoginMenu().show();
		close();
	}

	private static void close()
	{
		Console.close();
		System.exit(0);
	}
}
