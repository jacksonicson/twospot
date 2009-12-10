package org.prot.appserver.runtime.java;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.config.ServerMode;

public class AppDeployer extends AbstractLifeCycle
{
	private static final Logger logger = Logger.getLogger(AppDeployer.class);

	private AppInfo appInfo;

	private HandlerCollection contexts;
	private WebAppContext webAppContext;

	private DistributedSessionManager sessionManager;

	public void setAppInfo(AppInfo appInfo)
	{
		this.appInfo = appInfo;
	}

	public void setContexts(HandlerCollection contexts)
	{
		this.contexts = contexts;
	}

	public void doStart() throws Exception
	{
		webAppContext = null;
		deploy();

		// If we are running in development mode we also start a mock
		// implementaition of the portal. This implementation contains only core
		// services like the UserService (login gui)
		if (Configuration.getInstance().getServerMode() == ServerMode.DEVELOPMENT)
			deployDevelopment();
	}

	public void doStop() throws Exception
	{
		if (webAppContext != null)
		{
			webAppContext.stop();
		}
	}

	private void deployDevelopment() throws Exception
	{
		logger.info("Deploying development contexts");

		WebAppContext devContext = new WebAppContext();
		
		devContext.setWar("./devserver"); // WARN: Hardcoded
		devContext.setContextPath("/twospot");
		devContext.setDefaultsDescriptor("/etc/webdefault.xml");
		devContext.setTempDirectory(new File(Configuration.getInstance().getAppScratchDir()));
		devContext.setExtractWAR(false);
		devContext.setParentLoaderPriority(true); // Load everything from the
		devContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/jsp-api-[^/]*\\.jar$|.*/jsp-[^/]*\\.jar$");

		logger.debug("Adding and starting handler");
		contexts.addHandler(devContext);
		if (contexts.isStarted())
			contexts.start();
		logger.debug("Application handler started");

	}

	private void deploy() throws Exception
	{
		logger.debug("Deploying application");

		// Get the configuration
		JavaConfiguration runtimeConfig = (JavaConfiguration) appInfo.getRuntimeConfiguration();
		Configuration configuration = Configuration.getInstance();

		// Create a new Context for the web application
		webAppContext = new WebAppContext();
		webAppContext.setWar(configuration.getAppDirectory());
		webAppContext.setContextPath("/");

		// Configure the system classes (application can see this classes)
		String[] ownSystemClasses = { "org.prot.app." };
		webAppContext.setSystemClasses(ownSystemClasses);

		// Configure the server classes (application can not see this classes)
		String[] ownServerClasses = { "org.prot.appserver." };
		webAppContext.setServerClasses(ownServerClasses);

		// Configure the session handler (Depends on the app configuration)
		if (runtimeConfig.isUseDistributedSessions())
		{
			SessionHandler sessionHandler = new SessionHandler(sessionManager);
			webAppContext.setSessionHandler(sessionHandler);
			logger.info("Using distributed sesssion manager");
		}

		// Custom error handling
		webAppContext.setErrorHandler(new ErrorHandler());

		// Set the scratch directory for this web application
		webAppContext.setTempDirectory(new File(configuration.getAppScratchDir()));

		// Don't extract web archives
		webAppContext.setExtractWAR(false);

		// Used by the web archiver (don't use that)
		webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/jsp-api-[^/]*\\.jar$|.*/jsp-[^/]*\\.jar$");

		// All classes from the parent class loader are visible
		webAppContext.setParentLoaderPriority(false);

		// Default web application configuration descriptor
		webAppContext.setDefaultsDescriptor("/etc/webdefault.xml");

		// Register and start the context
		logger.debug("Adding and starting handler");
		contexts.addHandler(webAppContext);
		if (contexts.isStarted())
			contexts.start();
		logger.debug("Application handler started");
	}

	public synchronized void setSessionManager(DistributedSessionManager sessionManager)
	{
		this.sessionManager = sessionManager;
	}
}
