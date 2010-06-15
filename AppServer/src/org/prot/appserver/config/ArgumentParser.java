/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.appserver.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.log4j.Logger;

public class ArgumentParser {
	private static CommandLine cmd = null;

	public static void dump() {
		Logger logger = Logger.getLogger(ArgumentParser.class);

		if (cmd == null) {
			logger.error("Missing starup arguments");
			return;
		}

		logger.info("Startup arguments:");
		for (Option option : cmd.getOptions()) {
			logger.info(option.getOpt() + " = " + option.getValue());
		}
	}

	public static void parseArguments(String args[]) {
		Map<String, String> parsed = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			System.out.println("arg: " + args[i]);
			if (args[i].startsWith("-")) {
				String key = args[i];
				i++;
				String value = args[i];
				parsed.put(key, value);
			}
		}

		Configuration config = Configuration.getInstance();
		config.setAppId(parsed.get("-appId"));
		config.setAppServerPort(Integer.parseInt(parsed.get("-appSrvPort")));

		if (parsed.containsKey("-baseDir"))
			config.setWorkingDirectory(parsed.get("-baseDir"));

		if (parsed.containsKey("-workDir"))
			config.setWorkingDirectory(parsed.get("-workDir"));

		if (parsed.containsKey("-stdio"))
			config.setEnableStdOut(Boolean.parseBoolean(parsed.get("-stdio")));

		if (parsed.containsKey("-controller"))
			config.setRequiresController(Boolean.parseBoolean(parsed.get("-controller")));

		if (parsed.containsKey("-token")) {
			config.setPrivileged(true);
			config.setAuthenticationToken(parsed.get("-token"));
		}

		// Finish the configuration process
		config.postInitialize();
	}
	//
	// @SuppressWarnings("static-access")
	// public static void parseArguments_old(String args[]) {
	// Options options = new Options();
	//
	// // AppId
	// Option appId =
	// OptionBuilder.withArgName("application id").hasArg().isRequired().create("appId");
	// options.addOption(appId);
	//
	// // Port for this AppServer
	// Option appServerPort =
	// OptionBuilder.withArgName("appServer port").hasArg().isRequired().create(
	// "appSrvPort");
	// options.addOption(appServerPort);
	//
	// // Should the AppServer use the STDOUT and STDIN streams
	// Option stdio =
	// OptionBuilder.withArgName("write sdtout messages").hasArg().create("stdio");
	// options.addOption(stdio);
	//
	// // Should the AppServer shutdown if the connection to the local
	// // Controller breaks
	// Option controller =
	// OptionBuilder.withArgName("requires the Controller").hasArg()
	// .create("controller");
	// options.addOption(controller);
	//
	// // Privileged applications must authenticate with the controller
	// Option authenticationToken = OptionBuilder.withArgName(
	// "authentication token used to authenticate privileged applications").hasArg().create("token");
	// options.addOption(authenticationToken);
	//
	// // Base directory where the application resides
	// Option baseDir = OptionBuilder.withArgName(
	// "base directory where the application and yaml configuration reside").hasArg().create(
	// "baseDir");
	// options.addOption(baseDir);
	//
	// // Location of the decompressed application (for the standalone mode)
	// Option workingDirectory =
	// OptionBuilder.withArgName("application directory").hasArg().create(
	// "workDir");
	// options.addOption(workingDirectory);
	//
	// try {
	// // Parse the command line
	// CommandLineParser parser = new GnuParser();
	// CommandLine cmd = parser.parse(options, args);
	// ArgumentParser.cmd = cmd;
	//
	// // Central configuration
	// Configuration config = Configuration.getInstance();
	//
	// // Fill the configuration
	// config.setAppId(cmd.getOptionValue("appId"));
	// config.setAppServerPort(new Integer(cmd.getOptionValue("appSrvPort")));
	//
	// if (cmd.hasOption("stdio"))
	// config.setEnableStdOut(Boolean.parseBoolean(cmd.getOptionValue("stdio")));
	//
	// if (cmd.hasOption("controller"))
	// config.setRequiresController(Boolean.parseBoolean(cmd.getOptionValue("controller")));
	//
	// if (cmd.hasOption("token")) {
	// config.setPrivileged(true);
	// config.setAuthenticationToken(cmd.getOptionValue("token"));
	// }
	//
	// if (cmd.hasOption("baseDir")) {
	// config.setWorkingDirectory(cmd.getOptionValue("baseDir"));
	// }
	//
	// if (cmd.hasOption("workDir")) {
	// config.setWorkingDirectory(cmd.getOptionValue("workDir"));
	// }
	//
	// // Finish the configuration process
	// config.postInitialize();
	//
	// } catch (ParseException e) {
	// HelpFormatter formatter = new HelpFormatter();
	// formatter.printHelp("AppServer", options);
	//
	// // Exit process
	// System.exit(0);
	// } catch (NumberFormatException e) {
	// HelpFormatter formatter = new HelpFormatter();
	// formatter.printHelp("AppServer", options);
	//
	// // Exit process
	// System.exit(0);
	// }
	// }
}
