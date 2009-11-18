package org.prot.appserver;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.appfetch.AppFetcher;
import org.prot.appserver.config.AppConfigurer;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.config.ConfigurationException;
import org.prot.appserver.extract.AppExtractor;
import org.prot.appserver.runtime.AppRuntime;
import org.prot.appserver.runtime.NoSuchRuntimeException;
import org.prot.appserver.runtime.RuntimeRegistry;

public class ServerLifecycle
{
	private static final Logger logger = Logger.getLogger(ServerLifecycle.class);

	private static final String SERVER_ONLINE = "server online";

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
			System.exit(1);
		}
	}

	public void configure()
	{
		logger.info("Loading app configuration");

		// Configure
		try
		{
			this.appInfo = appConfigurer.configure(configuration.getAppDirectory(), null);
		} catch (ConfigurationException e)
		{
			logger.error("Configuration failed", e);
			System.exit(1);
		}
	}

	public void startManagement()
	{
		// TODO: Start and register the JMX-Beans here
	}

	public void launchRuntime()
	{
		logger.info("Launching runtime");

		try
		{
			AppRuntime runtime = runtimeRegistry.getRuntime(appInfo.getRuntime());
			runtime.launch(this.appInfo);

		} catch (NoSuchRuntimeException e)
		{
			logger.error("Failed launching the runtime", e);
			System.exit(1);
		} catch (Exception e)
		{
			logger.error("Failed launching the runtime", e);
			System.exit(1);
		}

		logger.info("Server is online: " + SERVER_ONLINE);
		// Use the original stdio to tell the controller
		IODirector.getInstance().forcedStdOutPrintln(SERVER_ONLINE);
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
