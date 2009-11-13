package org.prot.controller.manager;

import org.eclipse.jetty.util.thread.ThreadPool;

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

	public AppInfo requireApp(String appId)
	{
		AppInfo appInfo = registry.registerApp(appId);

		AppState todo = null;

		synchronized (appInfo)
		{
			// Operations depends on app status
			switch (appInfo.getStatus())
			{
			case ONLINE:
				return appInfo;
			case STARTING:
				todo = AppState.STARTING;
				break;
			case OFFLINE:
			case STALE:
				appInfo.setStatus(AppState.STARTING);
				todo = AppState.OFFLINE;
				break;
			}
		}

		switch (todo)
		{
		case OFFLINE:
			if (startApp(appInfo))
				return null;
			break;
		case STARTING:
			if (monitor.waitForApplication(appInfo))
				return null;
			break;
		}

		return appInfo;
	}

	public void reportStaleApp(String appId)
	{
		AppInfo appInfo = registry.getAppInfo(appId);
		synchronized (appInfo)
		{
			appInfo.setStatus(AppState.STALE);
		}
	}

	private boolean startApp(AppInfo appInfo)
	{
		// Enqueue the process start
		monitor.startProcess(appInfo);

		// Wait until the AppServer is online
		return monitor.waitForApplication(appInfo);
	}

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}
}
