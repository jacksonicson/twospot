package org.prot.appserver;

import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.prot.appserver.app.AppInfo;

public class AppDeployer extends AbstractLifeCycle
{
	private AppInfo appInfo;

	private HandlerCollection contexts;
	private WebAppContext webAppContext;

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo; 
	}
	
	public void setContexts(HandlerCollection contexts) {
		this.contexts = contexts; 
	}
	
	public void doStart() throws Exception
	{
		webAppContext = null; 
		deploy();
	}

	public void doStop() throws Exception
	{
		if(webAppContext != null) {
			webAppContext.stop(); 
		}
	}

	private void deploy() throws Exception
	{
		webAppContext = new WebAppContext();
		webAppContext.setContextPath(Configuration.getInstance().getAppDirectory());
		// context.setDefaultsDescriptor(defaultsDescriptor);
		// context.setConfigurationClasses(configurations)
		webAppContext.setExtractWAR(false);
		webAppContext.setParentLoaderPriority(true);

		contexts.addHandler(webAppContext);
		if (contexts.isStarted())
			contexts.start();
	}
}
