package org.prot.controller.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class AppProcess implements Runnable
{
	private AppInfo appInfo;

	private Process process;

	private BufferedReader stdInStream;

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
			// start the process
			this.process = procBuilder.start();

			// update status
			this.appInfo.setStatus(AppState.STARTING);

		} catch (IOException e)
		{
			e.printStackTrace();
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

	public void waitForAppServer()
	{
		try
		{
			// create IO streams
			stdInStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
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
			e.printStackTrace();
		}
	}

	public AppProcess(AppInfo appInfo)
	{
		this.appInfo = appInfo;
	}

	String line = "";

	@Override
	public void run()
	{
		System.out.println("Running...");

		// try
		// {
		// // read bytes
		// int len;
		// byte[] buffer = new byte[1024];
		// len = stdInStream.read(buffer);
		//			
		// line += new String(buffer);
		// while(line.indexOf('\n') > -1) {
		// int index = line.indexOf('\n');
		// String subline = line.substring(0, index);
		// line = line.substring(index + 1);
		//				
		// if(subline.equals("server started")) {
		// synchronized(appInfo) {
		// appInfo.setStatus(AppState.ONLINE);
		// appInfo.notify();
		// }
		// }
		// }
		//			
		// // output bytes
		// System.out.write(buffer, 0, len);
		//			
		// } catch (IOException e1)
		// {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		// if(appInfo.getStatus() == AppState.STARTING) {
		// synchronized(appInfo) {
		//				
		// try
		// {
		// Thread.sleep(5000);
		// } catch (InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		//				
		// appInfo.setStatus(AppState.ONLINE);
		// appInfo.notifyAll();
		// }
		// }

	}
}
