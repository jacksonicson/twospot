package org.prot.controller.manager;

import java.util.HashSet;
import java.util.Set;

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

	private enum Todo
	{
		START, WAIT
	};

	public AppInfo requireApp(String appId)
	{
		// Get or register the AppServer
		AppInfo appInfo = registry.getOrRegisterApp(appId);

		// Find and kill all idle AppServers
		Set<AppInfo> killed = registry.tick();
		if (killed != null)
			monitor.killProcess(killed);

		// Todo-Information
		Todo todo = null;

		// Simple state machine for managing the AppServer lifecycle
		synchronized (appInfo)
		{
			// Operations depends on app status
			switch (appInfo.getStatus())
			{
			case ONLINE:
				// Don't change the state
				return appInfo;
			case STARTING:
				// Don't change the state
				todo = Todo.WAIT;
				break;
			case OFFLINE:
			case STALE:
				// Change the state to STARTING
				appInfo.setStatus(AppState.STARTING);
				todo = Todo.START;
				break;
			}
		}

		// Is indirectly synchronized by the previous state selection
		switch (todo)
		{
		case START:
			// Start the application. Returns true if a continuation is used
			if (startApp(appInfo))
				return null;
			break;
		case WAIT:
			// Waits until the application is ONLINE. Returns true if a
			// continuation is used
			if (monitor.waitForApplication(appInfo))
				return null;
			break;
		}

		return appInfo;
	}

	public void killApp(String appId)
	{
		AppInfo appInfo = registry.getAppInfo(appId);
		assert(appInfo != null); 

		// Update the state
		appInfo.setStatus(AppState.KILLED);
		
		// Cleanup the registry
		registry.cleanup();
		
		// Shedule the termination
		Set<AppInfo> killed = new HashSet<AppInfo>();
		killed.add(appInfo); 
		monitor.killProcess(killed);
	}
	
	public void reportStaleApp(String appId)
	{
		AppInfo appInfo = registry.getAppInfo(appId);

		// Cannot change the state for an unexisting application
		if (appInfo == null)
			return;

		appInfo.setStatus(AppState.STALE);
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
