package org.prot.controller.manager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

class AppProcess
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

	private void stopAndClean()
	{
		process.destroy();
		process = null;
	}

	public void kill()
	{
		this.appInfo.setStatus(AppState.OFFLINE);
		stopAndClean();
	}

	public void startOrRestart()
	{
		// Kill the old process if exists
		if (process != null)
			stopAndClean();

		// build command
		List<String> command = new LinkedList<String>();
		command.add("java");
		command.add("-classpath");
		command.add(loadClasspath2());

		command.add("org.prot.appserver.Main");

		command.add("-appId");
		command.add(appInfo.getAppId());

		command.add("-ctrlPort");
		command.add("8079");

		command.add("-appSrvPort");
		command.add(appInfo.getPort() + "");

		System.out.println(command);

		// configure the process
		ProcessBuilder procBuilder = new ProcessBuilder();
		procBuilder.directory(new File("../AppServer"));
		procBuilder.command(command);
		procBuilder.redirectErrorStream(true);

		try
		{
			// Start the process
			this.process = procBuilder.start();

			// Wait until the Server running
			waitForAppServer();
			this.appInfo.setStatus(AppState.ONLINE);

		} catch (IOException e)
		{
			// Update status
			this.appInfo.setStatus(AppState.FAILED);

			// Log the error
			logger.error("Could not start a new server process - AppId: " + appInfo.getAppId() + " Command: "
					+ command.toString(), e);
		}

	}

	private String loadClasspath2()
	{
		String lib = "../Libs";
		String a = "" + lib + "/lib/jetty-7.0.0/servlet-api-2.5.jar;" + lib
				+ "/lib/apache_commons/commons-logging-1.1.1.jar;" + lib
				+ "/lib/snakeyaml-1.5/snakeyaml-1.5.jar;" + lib + "/lib/jython-2.5.1/jython.jar;" + lib
				+ "/lib/log4j-1.2.15/log4j-1.2.15.jar;" + lib
				+ "/lib/spring-framework-2.5.6.SEC01/modules/spring-core.jar;" + lib
				+ "/lib/spring-framework-2.5.6.SEC01/spring.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-deploy-7.0.0.v20091005.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-http-7.0.0.v20091005.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-io-7.0.0.v20091005.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-server-7.0.0.v20091005.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-servlet-7.0.0.v20091005.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-servlets-7.0.0.v20091005.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-util-7.0.0.v20091005.jar;" + lib
				+ "/lib/apache-cli-1.2/commons-cli-1.2.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-client-7.0.0.v20091005.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-webapp-7.0.0.v20091005.jar;" + lib
				+ "/lib/jetty-7.0.0/jetty-continuation-7.0.0.v20091005.jar;";

		a += "bin";

		return a;

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

	ReadableByteChannel source;
	WritableByteChannel destination;

	ByteArrayOutputStream outstream;
	ByteBuffer buffer = ByteBuffer.allocateDirect(128 * 1024);
	void fetchStreams() throws Exception
	{
		System.out.println("FETCH"); 
		if (process == null)
			return;
		
		if (source == null)
		{
			System.out.println("CREATING CHANNELS");
			InputStream in = process.getInputStream();
			source = Channels.newChannel(in);
			
			outstream = new ByteArrayOutputStream(5 * 1024);
			destination = Channels.newChannel(System.out);
		}
		
		
		while (source.read(buffer) != -1)
		{
			System.out.println("READ-----------------------------");
			
			buffer.flip();
			
			while(buffer.hasRemaining())
			{
				destination.write(buffer); 
			}
			
			System.out.println("D>OON-----------------------------"); 
			
//			while (buffer.hasRemaining())
//			{
////				destination.write(buffer);
//				buffer.clear();
//				System.out.println("Reading the buffer"); 
////				System.out.println("+++" + new String(outstream.toByteArray()));
//				
////				System.out.println("+++"+new String(buffer.compact().array()));
//			}
			buffer.clear();
		}
	}

	private void waitForAppServer()
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
					System.out.println(">>>" + line);
					logger.info("AppServer started: " + this.appInfo.getAppId());
					return;
				} else
				{
					System.out.println(">>>" + line);
				}
			}

		} catch (IOException e)
		{
			// Log the error
			logger.error("Could not start a new server process - AppId: " + appInfo.getAppId(), e);

			// Could not verify if server is online so kill it
			stopAndClean();

			// Update status
			this.appInfo.setStatus(AppState.FAILED);
		}
	}
}
