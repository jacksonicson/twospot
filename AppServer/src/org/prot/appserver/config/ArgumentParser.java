package org.prot.appserver.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

public class ArgumentParser
{
	private static CommandLine cmd = null;

	public static void dump()
	{
		Logger logger = Logger.getLogger(ArgumentParser.class);

		if (cmd == null)
		{
			logger.error("Missing starup arguments");
			return;
		}

		logger.info("Startup arguments:");
		for (Option option : cmd.getOptions())
		{
			logger.info(option.getOpt() + " = " + option.getValue());
		}
	}

	@SuppressWarnings("static-access")
	public static void parseArguments(String args[])
	{
		Options options = new Options();

		// AppId
		Option appId = OptionBuilder.withArgName("application id").hasArg().isRequired().create("appId");
		options.addOption(appId);

		// Port for this AppServer
		Option appServerPort = OptionBuilder.withArgName("appServer port").hasArg().isRequired().create(
				"appSrvPort");
		options.addOption(appServerPort);

		// Should the AppServer use the STDOUT and STDIN streams
		Option stdio = OptionBuilder.withArgName("write sdtout messages").hasArg().create("stdio");
		options.addOption(stdio);

		// Should the AppServer shutdown if the connection to the local
		// Controller breaks
		Option controller = OptionBuilder.withArgName("requires the Controller").hasArg()
				.create("controller");
		options.addOption(controller);

		// Privileged applications must authenticate with the controller
		Option authenticationToken = OptionBuilder.withArgName(
				"authentication token used to authenticate privileged applications").hasArg().create("token");
		options.addOption(authenticationToken);

		// Location of the decompressed application (for the standalone mode)
		Option workingDirectory = OptionBuilder.withArgName("application directory").hasArg().create(
				"workDir");
		options.addOption(workingDirectory);

		try
		{
			// Parse the command line
			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, args);
			ArgumentParser.cmd = cmd;

			// Central configuration
			Configuration config = Configuration.getInstance();

			// Fill the configuration
			config.setAppId(cmd.getOptionValue("appId"));
			config.setAppServerPort(new Integer(cmd.getOptionValue("appSrvPort")));

			if (cmd.hasOption("stdio"))
				config.setEnableStdOut(Boolean.parseBoolean(cmd.getOptionValue("stdio")));

			if (cmd.hasOption("controller"))
				config.setRequiresController(Boolean.parseBoolean(cmd.getOptionValue("controller")));

			if (cmd.hasOption("token"))
			{
				config.setPrivileged(true);
				config.setAuthenticationToken(cmd.getOptionValue("token"));
			}

			if (cmd.hasOption("workDir"))
			{
				config.setWorkingDirectory(cmd.getOptionValue("workDir"));
			}

			// Finish the configuration process
			config.postInitialize();

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