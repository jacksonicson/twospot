package org.prot.controller.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

class AppProcess
{
	// Logger
	private static final Logger logger = Logger.getLogger(AppProcess.class);

	// Info sent by appserver
	private static final String SERVER_ONLINE = "server online";

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
		logger.info("killing the AppServer-process: " + appInfo.getAppId());

		process.destroy();
		process = null;
	}

	public void kill()
	{
		this.appInfo.setStatus(AppState.OFFLINE);
		stopAndClean();
	}

	public void startOrRestart() throws IOException
	{
		// Check if the process should be killed
		if (appInfo.getStatus() == AppState.KILLED)
		{
			stopAndClean();
			return;
		}

		// Kill the old process if exists
		if (process != null)
			stopAndClean();

		// build command
		List<String> command = new LinkedList<String>();
		command.add("java");
		command.add("-classpath");
		command.add(loadGeneratedClasspath());

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
		{
			c += cmd + " ";
		}
		System.out.println("executing: " + c);

		// configure the process
		ProcessBuilder procBuilder = new ProcessBuilder();
		procBuilder.directory(new File("../AppServer/"));
		procBuilder.command(command);
		procBuilder.redirectErrorStream(true);

		try
		{
			// Start the process
			this.process = procBuilder.start();

			// Wait until the Server running
			waitForAppServer();

			// Update the AppServer state
			this.appInfo.setStatus(AppState.ONLINE);

		} catch (IOException e)
		{
			// Log the error
			logger.error("Could not start a new server process (AppId: " + appInfo.getAppId() + " Command: "
					+ command.toString() + ")", e);

			throw e;
		}

	}

	private String loadClasspath()
	{
		File libs = new File("../Libs/");
		String classpath = crawlDir(libs) + ";";

		File appServer = new File("../AppServer/bin");
		classpath += appServer.getAbsolutePath() + ";";

		File utils = new File("../Util/bin");
		classpath += utils.getAbsolutePath() + ";";

		File controller = new File("../Controller/bin");
		classpath += controller.getAbsolutePath() + ";";

		System.out.println("Classpath: " + classpath);

		return classpath;
	}

	private String crawlDir(File dir)
	{
		String jars = "";

		for (File subdir : dir.listFiles())
		{

			if (subdir.isDirectory())
			{
				String subjar = crawlDir(subdir);
				jars += subjar;
			} else
			{
				String filename = subdir.getName();
				if (filename.lastIndexOf(".") > 0)
					filename = filename.substring(filename.lastIndexOf("."));
				if (filename.equals(".jar"))
					jars += subdir.getAbsolutePath() + ";";
			}
		}

		return jars;
	}

	private List<String> readClasspath()
	{
		List<String> classpath = new ArrayList<String>();
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(AppProcess.class
					.getResourceAsStream("/classpath.txt")));

			String buffer = "";
			while ((buffer = reader.readLine()) != null)
			{
				classpath.add(buffer);
			}

			reader.close();

		} catch (IOException e)
		{
			logger.error("Could not read the classpath", e);
		}

		return classpath;
	}

	private String loadGeneratedClasspath()
	{
		final String separator;
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
			separator = ";";
		else
			separator = ":";

		List<String> libs = readClasspath();
		String libLocation = "../";
		String classpath = "";

		for (String lib : libs)
		{
			classpath += libLocation + lib + separator;
		}

		File appServer = new File("../AppServer/bin");
		classpath += appServer.getAbsolutePath() + separator;

		File utils = new File("../Util/bin");
		classpath += utils.getAbsolutePath() + separator;

		File controller = new File("../Controller/bin");
		classpath += controller.getAbsolutePath() + separator;

		return classpath;
	}

	private void waitForAppServer() throws IOException
	{
		// create IO streams
		BufferedReader stdInStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

		try
		{
			// read the input stream until SERVER_ONLINE sequence is found
			String line = "";
			while ((line = stdInStream.readLine()) != null)
			{
				logger.debug("from: " + appInfo.getAppId() + " >" + line);

				if (line.equalsIgnoreCase(SERVER_ONLINE))
				{
					logger.info("AppServer is online: " + this.appInfo.getAppId());
					return;
				}
			}

		} catch (IOException e)
		{
			// Log the error
			logger.error("Could not start a new server process - AppId: " + appInfo.getAppId(), e);

			// Could not verify if server is online so kill it
			stopAndClean();

			throw e;
		} finally
		{
			// Close the input stream
			stdInStream.close();
		}
	}
}
