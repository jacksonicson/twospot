package org.prot.controller.manager2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedList;
import java.util.List;

public class AppProcess implements Runnable
{
	private AppInfo appInfo;

	private Process process;

	private ByteArrayOutputStream stdOutStream;
	private ByteArrayOutputStream errOutStream;

	private InputStream stdInStream;
	private InputStream errInStream;

	public AppInfo getOwner() {
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
		procBuilder.redirectErrorStream(false);

		try
		{
			// start the process
			this.process = procBuilder.start();

			// update status
			this.appInfo.setStatus(AppState.STARTING); 
			
		} catch (IOException e)
		{
			this.appInfo.setStatus(AppState.FAILED); 
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
		this.stdOutStream = null;
		this.errOutStream = null;
	}

	public AppProcess(AppInfo appInfo)
	{
		this.appInfo = appInfo;

		stdOutStream = new ByteArrayOutputStream();
		errOutStream = new ByteArrayOutputStream();

		stdInStream = this.process.getInputStream();
		errInStream = this.process.getErrorStream();
	}

	@Override
	public void run()
	{
		System.out.println("Running..."); 
		
		/* ReadableByteChannel channel = Channels.newChannel(stdInStream);
		ByteBuffer input = ByteBuffer.allocate(1024);
		try
		{
			int number = channel.read(input);
			stdOutStream.write(input.array(), 0, number);

			if (number == -1)
			{

			}

		} catch (IOException e)
		{
			e.printStackTrace();
		} */
	}
}
