package org.prot.appserver;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.xml.sax.SAXException;

public class Main {

	public Main() {
		try {

			InputStream configFile = Main.class.getResourceAsStream("/etc/jetty/configuration.xml");
			XmlConfiguration config = new XmlConfiguration(configFile);
			Server server = (Server) config.configure();
			server.start();

			new Monitor();

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void parseArguments(String[] args) {
		Options options = new Options();

		Option appId = OptionBuilder.withArgName("application id").hasArg().isRequired().create("appId");
		options.addOption(appId);

		Option controlPort = OptionBuilder.withArgName("control port").hasArg().isRequired().create(
				"ctrlPort");
		options.addOption(controlPort);

		try {
			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, args);

			System.out.println("AppId: " + cmd.getOptionValue("appId"));
			System.out.println("ControlPort: " +cmd.getOptionValue("ctrlPort"));

		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("AppServer", options);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		parseArguments(args);
		
		// TODO: use spring ioc
		new Main();
	}
}
