package org.prot.appserver;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.prot.appserver.config.Configuration;

public class ArgumentParser
{
	public static void parseArguments(String args[])
	{
		Options options = new Options();

		Option appId = OptionBuilder.withArgName("application id").hasArg().isRequired().create("appId");
		options.addOption(appId);

		Option controlPort = OptionBuilder.withArgName("control port").hasArg().isRequired().create(
				"ctrlPort");
		options.addOption(controlPort);

		Option appServerPort = OptionBuilder.withArgName("appServer port").hasArg().isRequired().create(
				"appSrvPort");
		options.addOption(appServerPort);

		Option stdio = OptionBuilder.withArgName("write sdtout messages").hasArg().create("stdio");
		options.addOption(stdio);

		try
		{
			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, args);

			Configuration config = Configuration.getInstance();
			config.setAppId(cmd.getOptionValue("appId"));
			config.setControlPort(new Integer(cmd.getOptionValue("ctrlPort")));
			config.setAppServerPort(new Integer(cmd.getOptionValue("appSrvPort")));

			if (cmd.hasOption("stdio"))
				config.setEnableStdOut(Boolean.parseBoolean(cmd.getOptionValue("stdio")));

		} catch (ParseException e)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("AppServer", options);
		
			// Exit process
			System.exit(0);
		} catch (NumberFormatException e)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("AppServer", options);
			
			// Exit process
			System.exit(0);
		}
	}
}
