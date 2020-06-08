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
		Option serverAddress = new Option("H", "hosts", true, "List of server hostname:port.");
		serverAddress.setType(String.class);
		serverAddress.setArgName("HOSTLIST");
		options.addOption(serverAddress);
		return options;
	}

	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("app [-h | --help] [-H <HOSTLIST> | --hosts <HOSTLIST>]", options);
			close();
		}
		if (cmd.hasOption("hosts")) {
			String str = cmd.getOptionValue("hosts").trim();
			if (!str.matches("^[a-zA-z0-9._-]+(:[0-9]{1,5})?$")) {
				System.err.println("Invalid host list specified. Connecting to default cluster 172.16.1.{35,39,43}:8888.");
				launchCLI(cmd.getArgs());
			}
			String[] servers = str.split(",");
			for (int s = 0; s < servers.length; s++) {
				String[] server = str.split(":", 2);
				int port;
				try {
					port = server.length > 1 ? Integer.parseInt(server[1]) : 8888;
				} catch (NumberFormatException ex) {
					System.err.println("Invalid port specified for '" + server[0] + "'. Using default: 8888.");
					port = 8888;
				}
				Protocol.getInstance().addServer(server[0], port);
			}
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
