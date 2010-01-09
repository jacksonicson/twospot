package org.prot.controller.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.controller.config.Configuration;

class ProcessHandler
{
	// Logger
	private static final Logger logger = Logger.getLogger(ProcessHandler.class);

	// Info sent by appserver
	private static final String SERVER_ONLINE = "server online";
	private static final String SERVER_FAILED = "server failed";

	private void stopAndClean(AppProcess process)
	{
		if (process.getProcess() == null)
			return;

		try
		{
			process.getProcess().exitValue();
		} catch (IllegalThreadStateException e)
		{
			logger.debug("Process is still running");
			try
			{
				process.getProcess().destroy();
				process.setProcess(null);
			} catch (Exception killerr)
			{
				logger.error("Could not stop process: ", killerr);
			}
		}
	}

	void stop(AppProcess appProcess)
	{
		logger.debug("Stopping AppServer...");
		stopAndClean(appProcess);
	}

	boolean execute(AppInfo appInfo, AppProcess appProcess)
	{
		logger.debug("Starting AppServer...");

		// Kill the old process if exists
		stopAndClean(appProcess);

		// Create the command line
		List<String> command = createCommand(appInfo);

		// configure the process
		ProcessBuilder procBuilder = new ProcessBuilder();
		procBuilder.command(command);
		procBuilder.redirectErrorStream(true);

		try
		{
			// Start the process
			logger.debug("Starting process...");
			Process process = procBuilder.start();
			appProcess.setProcess(process);

			// Wait until the Server running
			logger.debug("Waiting for AppServer...");
			waitForAppServer(process);

			// Update the AppServer state
			logger.debug("AppServer is ONLINE");
			return true;

		} catch (IOException e)
		{
			// Log the error
			logger.error("Could not start a new server process (AppId: " + appInfo.getAppId() + " Command: "
					+ command.toString() + ")", e);

			stopAndClean(appProcess);
			return false;
		}
	}

	private List<String> createCommand(AppInfo appInfo)
	{
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

		if (appInfo.isPrivileged())
		{
			command.add("-token");
			command.add(appInfo.getProcessToken());
		}

		String c = "";
		for (String cmd : command)
			c += cmd + " ";
		logger.debug("Command: " + c);

		return command;
	}

	private List<String> readClasspath()
	{
		List<String> classpath = new ArrayList<String>();
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(ProcessHandler.class
					.getResourceAsStream("/cpAppServer.txt")));

			String buffer = "";
			while ((buffer = reader.readLine()) != null)
				classpath.add(buffer);

			reader.close();

		} catch (IOException e)
		{
			logger.error("Could not read the classpath", e);
		} catch (NullPointerException e)
		{
			logger.error("Could not read classpath", e);
		}

		return classpath;
	}

	private List<String> loadVmOptions()
	{
		String config = Configuration.getConfiguration().getVmOptions();
		logger.debug("Using JVM options: " + config);

		String[] options = config.split("\\s");
		List<String> list = new ArrayList<String>();
		for (String option : options)
		{
			logger.trace("JVM option: " + option);
			list.add(option);
		}

		return list;
	}

	private String loadClasspath()
	{
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

	private boolean waitForAppServer(Process process) throws IOException
	{
		// create IO streams
		BufferedReader stdInStream = null;

		try
		{
			stdInStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

			// Read the input stream until SERVER_ONLINE sequence is found
			String line = "";
			while ((line = stdInStream.readLine()) != null)
			{
				logger.debug("appserver> " + line);

				if (line.equalsIgnoreCase(SERVER_ONLINE))
				{
					logger.info("AppServer is ONLINE");
					return true;
				} else if (line.equalsIgnoreCase(SERVER_FAILED))
				{
					logger.info("AppServer FAILED");
					throw new IOException("AppServer FAILED");
				}
			}

			// AppServer is not online - we did not recive SERVER_ONLINE or
			// SERVER_FAILED
			logger.info("AppServer is NOT ONLINE");
			throw new IOException("AppServer FAILED");

		} catch (IOException e)
		{
			// Log the error
			logger.error("Error while starting AppServer");

			// Rethrow this exception
			throw e;

		} finally
		{
			try
			{
				// Close the input stream
				if (stdInStream != null)
					stdInStream.close();
			} catch (IOException e)
			{
				logger.trace(e);
			}
		}
	}
}
