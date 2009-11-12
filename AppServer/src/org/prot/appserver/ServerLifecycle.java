package org.prot.appserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.appfetch.AppFetcher;
import org.prot.appserver.config.AppConfigurer;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.extract.AppExtractor;
import org.prot.appserver.runtime.AppRuntime;
import org.prot.appserver.runtime.NoSuchRuntimeException;
import org.prot.appserver.runtime.RuntimeRegistry;

public class ServerLifecycle
{
	private static final Logger logger = Logger.getLogger(ServerLifecycle.class);

	private Configuration configuration;

	private AppFetcher appFetcher;
	private AppExtractor appExtractor;
	private AppConfigurer appConfigurer;
	private RuntimeRegistry runtimeRegistry;

	private AppInfo appInfo = null;

	public void start()
	{
		logger.info("Starting AppServer");

		configuration = Configuration.getInstance();

		loadApp();
		extractApp();
		configure();
		startManagement();
		launchRuntime();
	}

	public void loadApp()
	{
		logger.info("Loading app archive");

		String appId = configuration.getAppId();
		appInfo = appFetcher.fetchApp(appId);
	}

	public void extractApp()
	{
		byte[] archive = appInfo.getWarFile();
		String destPath = configuration.getWorkingDirectory();
		String destDir = appInfo.getAppId();

		logger.info("Extracting app archive to: " + destPath + " dir:" + destDir);

		try
		{
			String appDirectory = appExtractor.extract(archive, destPath, destDir);
			configuration.setAppDirectory(appDirectory);
		} catch (IOException e)
		{
			logger.error("Error while extracting application package", e);
		}
	}

	public void configure()
	{
		logger.info("Loading app configuration");

		// Configure
		this.appInfo = appConfigurer.configure(configuration.getAppDirectory(), null);
	}

	public void startManagement()
	{
		// TODO:
	}

	public void launchRuntime()
	{
		logger.info("Launching runtime");
		
		// Creat a backuo of the System.out
		PrintStream outBackup = System.out;
		
		// A new Stream which drops everything
		PrintStream devNull = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException
			{
				// Do nothing
			}
		});
		
		// Redirect the err and out streams
		// INFO: The Log4j streams are *not* redirected
//		System.setOut(devNull);
//		System.setErr(devNull);
		
		// Finally launch the server with the new streams
		try
		{
			AppRuntime runtime = runtimeRegistry.getRuntime(appInfo.getRuntime());
			runtime.launch(this.appInfo);

		} catch (NoSuchRuntimeException e)
		{
			logger.error("Could not find the runtime configured in the YAML file", e);
		}

		// Use the original stdio to tell the controller
		outBackup.println("server started");
	}

	public void setAppFetcher(AppFetcher appFetcher)
	{
		this.appFetcher = appFetcher;
	}

	public void setAppExtractor(AppExtractor appExtractor)
	{
		this.appExtractor = appExtractor;
	}

	public void setRuntimeRegistry(RuntimeRegistry runtimeRegistry)
	{
		this.runtimeRegistry = runtimeRegistry;
	}

	public void setAppConfigurer(AppConfigurer appConfigurer)
	{
		this.appConfigurer = appConfigurer;
	}
}
