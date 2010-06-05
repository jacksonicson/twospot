package org.prot.controller.app.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.config.Configuration;

public class JavaAppLauncher implements AppLauncher {

	private static final Logger logger = Logger.getLogger(JavaAppLauncher.class);

	@Override
	public List<String> createCommand(AppInfo appInfo, String baseDir) {
		List<String> command = new LinkedList<String>();
		command.add("java");

		command.addAll(loadVmOptions());

		command.add("-classpath");
		command.add(loadClasspath());

		command.add("org.prot.appserver.Main");

		command.add("-appId");
		command.add(appInfo.getAppId());

		command.add("-appSrvPort");
		command.add(appInfo.getPort() + "");

		command.add("-baseDir");
		command.add(baseDir);

		if (appInfo.isPrivileged()) {
			command.add("-token");
			command.add(appInfo.getProcessToken());
		}

		String c = "";
		for (String cmd : command)
			c += cmd + " ";
		logger.debug("Command: " + c);

		return command;
	}

	private List<String> loadVmOptions() {
		String config = Configuration.getConfiguration().getVmOptions();
		logger.debug("Using JVM options: " + config);

		String[] options = config.split("\\s");
		List<String> list = new ArrayList<String>();
		for (String option : options) {
			logger.trace("JVM option: " + option);
			list.add(option);
		}

		return list;
	}

	private String loadClasspath() {
		// Get the classpath separator
		final String separator = System.getProperty("path.separator");

		// Read the classpath file
		List<String> libs = readClasspath();
		String classpath = "";

		// Load the classpath prefix
		String prefix = Configuration.getConfiguration().getClasspathPrefix();

		// Build the classpath from the classpath file
		for (String lib : libs)
			classpath += prefix + lib + separator;

		// Add the additional classpath
		String additionalClasspath = Configuration.getConfiguration().getAdditionalClasspath();
		logger.debug("Using additional classpath: " + additionalClasspath);

		additionalClasspath = additionalClasspath.replace(":", separator);
		classpath += additionalClasspath;

		return classpath;
	}

	private List<String> readClasspath() {
		List<String> classpath = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(JavaAppLauncher.class
					.getResourceAsStream("/cpAppServer.txt")));

			String buffer = "";
			while ((buffer = reader.readLine()) != null)
				classpath.add(buffer);

			reader.close();

		} catch (IOException e) {
			logger.error("Could not read the classpath", e);
		} catch (NullPointerException e) {
			logger.error("Could not read classpath", e);
		}

		return classpath;
	}

	@Override
	public String getIdentifier() {
		return "JAVA";
	}
}
