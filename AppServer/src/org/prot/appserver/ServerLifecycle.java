package org.prot.appserver;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.appfetch.AppFetcher;
import org.prot.appserver.config.AppConfigurer;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.config.ConfigurationException;
import org.prot.appserver.extract.AppExtractor;
import org.prot.appserver.management.RuntimeManagement;
import org.prot.appserver.management.AppServerManager;
import org.prot.appserver.runtime.AppRuntime;
import org.prot.appserver.runtime.NoSuchRuntimeException;
import org.prot.appserver.runtime.RuntimeRegistry;

public class ServerLifecycle
{
	private static final Logger logger = Logger.getLogger(ServerLifecycle.class);

	private static final String SERVER_ONLINE = "server online";
	private static final String SERVER_FAILED = "server failed";

	private Configuration configuration;

	private AppFetcher appFetcher;
	private AppExtractor appExtractor;
	private AppConfigurer appConfigurer;
	private RuntimeRegistry runtimeRegistry;
	private AppServerManager appManager;

	private AppInfo appInfo = null;

	public void start()
	{
		logger.info("Starting AppServer...");

		configuration = Configuration.getInstance();

		loadApp();

		extractApp();

		configure();

		launchRuntime();

		manageApp();
	}

	private final void loadApp()
	{
		logger.info("Loading app archive");

		String appId = configuration.getAppId();
		appInfo = appFetcher.fetchApp(appId);

		if (appInfo == null)
		{
			logger.error("Error while fetching app archive: " + appId);

			// Use the original stdio to tell the controller
			IODirector.getInstance().forcedStdOutPrintln(SERVER_FAILED);

			System.exit(1);
		}
	}

	private final void extractApp()
	{
		byte[] archive = appInfo.getWarFile();
		String destPath = configuration.getAppDirectory();

		logger.info("Extracting app archive to: " + destPath);

		try
		{
			appExtractor.extract(archive, destPath, appInfo.getAppId());
		} catch (IOException e)
		{
			logger.error("Error while extracting application package", e);

			// Use the original stdio to tell the controller
			IODirector.getInstance().forcedStdOutPrintln(SERVER_FAILED);

			System.exit(1);
		}
	}

	private final void configure()
	{
		logger.info("Loading app configuration");

		try
		{
			// Does the general configuration and calls the runtime specific
			// configuerer
			this.appInfo = appConfigurer.configure(configuration.getAppDirectory());
		} catch (ConfigurationException e)
		{
			logger.error("Configuration failed", e);

			// Use the original stdio to tell the controller
			IODirector.getInstance().forcedStdOutPrintln(SERVER_FAILED);

			System.exit(1);
		}
	}

	private final void manageApp()
	{
		try
		{
			logger.debug("Registering runtime in the AppManager");

			AppRuntime runtime = runtimeRegistry.getRuntime(appInfo.getRuntime());
			RuntimeManagement management = runtime.getManagement();
			appManager.manage(management);

		} catch (NoSuchRuntimeException e)
		{
			logger.error("Could not get runtime", e);

			// Use the original stdio to tell the controller
			IODirector.getInstance().forcedStdOutPrintln(SERVER_FAILED);

			System.exit(1);
		}
	}

	private final void launchRuntime()
	{
		logger.info("Launching runtime");

		try
		{
			AppRuntime runtime = runtimeRegistry.getRuntime(appInfo.getRuntime());
			runtime.launch(this.appInfo);

		} catch (NoSuchRuntimeException e)
		{
			logger.error("Failed launching the runtime", e);

			// Use the original stdio to tell the controller
			IODirector.getInstance().forcedStdOutPrintln(SERVER_FAILED);

			System.exit(1);
		} catch (Exception e)
		{
			logger.error("Failed launching the runtime", e);

			// Use the original stdio to tell the controller
			IODirector.getInstance().forcedStdOutPrintln(SERVER_FAILED);

			System.exit(1);
		}

		logger.info("SERVER IS ONLINE: " + SERVER_ONLINE);
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

	public void setAppManager(AppServerManager appManager)
	{
		this.appManager = appManager;
	}
}
