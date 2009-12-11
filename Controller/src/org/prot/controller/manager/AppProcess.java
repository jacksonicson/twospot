package org.prot.controller.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.controller.config.Configuration;

class AppProcess
{
	// Logger
	private static final Logger logger = Logger.getLogger(AppProcess.class);

	// Info sent by appserver
	private static final String SERVER_ONLINE = "server online";
	private static final String SERVER_FAILED = "server failed";

	// AppInfo which belongs to this process
	private AppInfo appInfo;

	// Connection to the process
	private Process process;

	public AppProcess(AppInfo appInfo)
	{
		this.appInfo = appInfo;
	}

	public AppInfo getAppInfo()
	{
		return this.appInfo;
	}

	private void stopAndClean()
	{
		logger.info("Killing AppServer process: " + appInfo.getAppId());

		try
		{
			process.destroy();
			process = null;
		} catch (Exception e)
		{
			// Do nothing
		}
	}

	public void kill()
	{
		this.appInfo.setStatus(AppState.OFFLINE);
		stopAndClean();
	}

	public void startOrRestart() throws IOException
	{
		logger.debug("Starting new AppServer process");

		// Check if the process should be killed
		if (appInfo.getStatus() == AppState.KILLED)
		{
			logger.debug("Could not start AppServer - already killed");
			stopAndClean();
			return;
		}

		// Kill the old process if exists
		if (process != null)
			stopAndClean();

		// build command
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
		logger.debug("Executing command: " + c);

		// configure the process
		ProcessBuilder procBuilder = new ProcessBuilder();
		// procBuilder.directory(new File("../AppServer/"));
		procBuilder.command(command);
		procBuilder.redirectErrorStream(true);

		try
		{
			// Start the process
			logger.debug("ProcesssBuilder start process");
			this.process = procBuilder.start();

			// Wait until the Server running
			logger.debug("Waiting for AppServer now");
			waitForAppServer();

			// Update the AppServer state
			logger.debug("AppServer seems to be online");
			this.appInfo.setStatus(AppState.ONLINE);

		} catch (IOException e)
		{
			// Log the error
			logger.error("Could not start a new server process (AppId: " + appInfo.getAppId() + " Command: "
					+ command.toString() + ")", e);

			throw e;
		}
	}

	private List<String> readClasspath()
	{
		logger.debug("Reading classpath file");
		List<String> classpath = new ArrayList<String>();
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(AppProcess.class
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
		// Determine the classpath separator
		final String separator;
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
			separator = ";";
		else
			separator = ":";

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

	private boolean waitForAppServer() throws IOException
	{
		// create IO streams
		BufferedReader stdInStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

		try
		{
			// Read the input stream until SERVER_ONLINE sequence is found
			String line = "";
			while ((line = stdInStream.readLine()) != null)
			{
				logger.debug("appserver: " + appInfo.getAppId() + "> " + line);

				if (line.equalsIgnoreCase(SERVER_ONLINE))
				{
					logger.info("AppServer is ONLINE: " + this.appInfo.getAppId());
					return true;
				} else if (line.equalsIgnoreCase(SERVER_FAILED))
				{
					logger.info("AppServer FAILED" + this.appInfo.getAppId());
					throw new IOException("AppServer FAILED");
				}
			}

			// AppServer is not online - we did not recive SERVER_ONLINE or
			// SERVER_FAILED
			logger.info("AppServer is NOT ONLINE: " + this.appInfo.getAppId());
			throw new IOException("AppServer FAILED");

		} catch (IOException e)
		{
			// Log the error
			logger.error("Could not start a new server process - AppId: " + appInfo.getAppId());

			// Could not verify if server is online so kill it
			stopAndClean();

			// Rethrow this exception
			throw e;
		} finally
		{
			// Close the input stream
			try
			{
				stdInStream.close();
			} catch (IOException e)
			{
				// Do nothing
			}
		}
	}
}
