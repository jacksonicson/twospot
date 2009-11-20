package org.prot.appserver.runtime.java;

import org.eclipse.jetty.server.handler.HandlerCollection;
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
		webAppContext.setSystemClasses(ownSystemClasses);

		// Configure the server classes (application can not see this classes)
		String[] ownServerClasses = { "org.prot.appserver." };
		webAppContext.setServerClasses(ownServerClasses);
		
		
		webAppContext.setExtractWAR(false);
		webAppContext.setParentLoaderPriority(false); // Load everything from the server classpath
		webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/jsp-api-[^/]*\\.jar$|.*/jsp-[^/]*\\.jar$");

		contexts.addHandler(webAppContext);
		if (contexts.isStarted())
			contexts.start();
	}
}
