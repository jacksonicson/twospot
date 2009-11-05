package org.prot.controller.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

class AppProcess implements Runnable
{
	private static final Logger logger = Logger.getLogger(AppProcess.class);

	private AppInfo appInfo;

	private Process process;

	public AppProcess(AppInfo appInfo)
	{
		this.appInfo = appInfo;
	}

	public AppInfo getOwner()
	{
		return this.appInfo;
	}

	public void startOrRestart()
	{
		// if the AppServer is marked as stale shutdown the process
		if (appInfo.getStatus() == AppState.STALE)
			stopAndClean();

		// build command
		List<String> command = new LinkedList<String>();
		command.add("java");
		command.add("-classpath");
		command.add(loadClasspath());
		command.add("org.prot.appserver.Main");

		command.add("-appId");
		command.add(appInfo.getAppId());

		command.add("-ctrlPort");
		command.add("8079");

		command.add("-appSrvPort");
		command.add(appInfo.getPort() + "");

		// configure the process
		ProcessBuilder procBuilder = new ProcessBuilder();
		procBuilder.directory(new File("../AppServer/"));
		procBuilder.command(command);
		procBuilder.redirectErrorStream(true);

		try
		{
			// Update status
			this.appInfo.setStatus(AppState.STARTING);

			// Start the process
			this.process = procBuilder.start();

		} catch (IOException e)
		{
			// Update status
			this.appInfo.setStatus(AppState.FAILED);

			// Log the error
			logger.error("Could not start a new server process - AppId: " + appInfo.getAppId() + " Command: "
					+ command.toString(), e);
		}
	}

	private String loadClasspath()
	{
		File libs = new File("../Libs/");
		String classpath = crawlDir(libs);

		File appServer = new File("../AppServer/bin");
		classpath += appServer.getAbsolutePath();

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

	public void stopAndClean()
	{
		this.process.destroy();
	}

	// TODO: Inperformant und geht nicht mit mehreren Warte-Threads
	public void waitForAppServer()
	{
		try
		{
			// create IO streams
			BufferedReader stdInStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

			// read input
			String line = "";
			while ((line = stdInStream.readLine()) != null)
			{
				if (line.equals("server started"))
				{
					this.appInfo.setStatus(AppState.ONLINE);
					return;
				}
			}
			
		} catch (IOException e)
		{
			// Could not verify if server is online so kill it
			stopAndClean();
			
			// Update status
			this.appInfo.setStatus(AppState.FAILED);
			
			// Log the error
			logger.error("Could not start a new server process - AppId: " + appInfo.getAppId(), e);
		}
	}

	@Override
	public void run()
	{
		System.out.println("Running...");
	}
}
