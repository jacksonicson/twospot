package org.prot.controller.manager;

import org.eclipse.jetty.util.thread.ThreadPool;
import org.prot.controller.manager.exceptions.AppServerFailedException;

public class AppManager
{
	private ThreadPool threadPool;

	private AppRegistry registry;

	private AppMonitor monitor;

	public void init()
	{
		monitor = new AppMonitor(threadPool);
		registry = new AppRegistry();
	}

	public AppInfo requireApp(String appId) throws AppServerFailedException
	{
		AppInfo appInfo = registry.registerApp(appId);

		AppState tmpState = null;

		synchronized (appInfo)
		{
			// Operations depends on app status
			switch (appInfo.getStatus())
			{
			case ONLINE:
				return appInfo;
			case STARTING:
				tmpState = AppState.STARTING;
				break;
			case OFFLINE:
			case STALE:
				appInfo.setStatus(AppState.STARTING);
				tmpState = AppState.OFFLINE;
				break;
			}
		}

		switch (tmpState)
		{
		case OFFLINE:
			if(startApp(appInfo))
				return null; 
			break;
		case STARTING:
			if(waitForAppServer(appInfo))
				return null;
			break;
		}

		return appInfo;
	}

	public void reportStaleApp(String appId)
	{
		AppInfo appInfo = registry.getAppInfo(appId);
		if(appInfo == null)
			return;
		
		synchronized (appInfo)
		{
			if (appInfo != null)
				appInfo.setStatus(AppState.STALE);
		}
	}

	private boolean waitForAppServer(AppInfo appInfo)
	{
		return monitor.waitForApplication(appInfo);
	}

	private boolean startApp(AppInfo appInfo)
	{
		// Enqueue the process start
		monitor.startProcess(appInfo);

		// Wait until the AppServer is online
		return waitForAppServer(appInfo);
	}

	
	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}
}
