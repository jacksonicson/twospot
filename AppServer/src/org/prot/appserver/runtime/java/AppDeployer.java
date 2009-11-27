package org.prot.appserver.runtime.java;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.config.Configuration;

public class AppDeployer extends AbstractLifeCycle
{
	@SuppressWarnings("unused")
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
	}

	public void doStop() throws Exception
	{
		if (webAppContext != null)
		{
			webAppContext.stop();
		}
	}

	private void deploy() throws Exception
	{
		webAppContext = new WebAppContext();
		webAppContext.setWar(Configuration.getInstance().getAppDirectory());
		webAppContext.setContextPath("/");

		// Configure the system classes (application can see this classes)
		String[] ownSystemClasses = { "org.prot.app." };
//		webAppContext.setSystemClasses(ownSystemClasses);

		// Configure the server classes (application can not see this classes)
		String[] ownServerClasses = { "org.prot.appserver." };
//		webAppContext.setServerClasses(ownServerClasses);
		
		// Configure the session handler
		SessionHandler sessionHandler = new SessionHandler(sessionManager);
		// webAppContext.setSessionHandler(sessionHandler);
		
		webAppContext.setErrorHandler(new ErrorHandler());
		
		webAppContext.setExtractWAR(false);
		webAppContext.setParentLoaderPriority(true); // Load everything from the server classpath
		webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/jsp-api-[^/]*\\.jar$|.*/jsp-[^/]*\\.jar$");
		
		webAppContext.setDefaultsDescriptor("/etc/webdefault.xml");

		contexts.addHandler(webAppContext);
		if (contexts.isStarted())
			contexts.start();
	}

	public synchronized void setSessionManager(DistributedSessionManager sessionManager)
	{
		this.sessionManager = sessionManager;
	}
}
