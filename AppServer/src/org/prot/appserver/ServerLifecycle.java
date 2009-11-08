package org.prot.appserver;

import org.apache.log4j.Logger;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.appfetch.AppFetcher;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.extract.AppExtractor;
import org.prot.appserver.runtime.AppRuntime;
import org.prot.appserver.runtime.RuntimeRegistry;

public class ServerLifecycle
{
	private static final Logger logger = Logger.getLogger(ServerLifecycle.class);
	
	private Configuration configuration;

	private AppFetcher appFetcher;
	private AppExtractor appExtractor;
	private RuntimeRegistry runtimeRegistry;

	private AppInfo appInfo = null;
	private AppRuntime runtime = null; 
	
	public void start()
	{
		logger.info("Starting AppServer"); 
		
		configuration = Configuration.getInstance();

		loadApp();
		extractApp();
		configure();
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
		logger.info("Extracting app archive");
		
		byte[] archive = appInfo.getWarFile();
		String destPath = configuration.getWorkingDirectory();
		String destDir = configuration.getAppDirectory(); 
		appExtractor.extract(archive, destPath, destDir); 
	}

	public void configure()
	{
		logger.info("Loading app configuration");
		
		runtime = runtimeRegistry.getRuntime(); 
		runtime.loadConfiguration(appInfo); 
	}

	public void launchRuntime()
	{
		logger.info("Launching runtime");
		
		runtime.launch();
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
}
