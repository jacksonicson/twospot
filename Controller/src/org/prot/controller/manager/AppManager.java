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
		// Initialize
		monitor = new AppMonitor(threadPool);
		registry = new AppRegistry();
	}

	/**
	 * Is executed whenever a process/server with the specified appId is needed.
	 * If there is already a running server-process the method will do nothing.
	 * If there is no server, a existing server has crashed or a server is
	 * unreachable the method will start a new server.
	 * 
	 * @param appId
	 * @return
	 * @throws DuplicatedAppException
	 * @throws AppServerFailedException
	 */
	public AppInfo requireApp(String appId) throws AppServerFailedException
	{
		// Register or load existing AppInfo
		AppInfo appInfo = registry.registerApp(appId);

		boolean wait = false;
		boolean start = false; 
		
		synchronized(appInfo) 
		{
			// Operations depends on app status
			switch (appInfo.getStatus())
			{
			case ONLINE:
				return appInfo;
			case STARTING:
				wait = true; 
				break;
			case OFFLINE:
			case STALE:
				appInfo.setStatus(AppState.STARTING); 
				start = true; 
				break;
			}
		}
		
		if(start)
			startApp(appInfo);
		if(wait)
			waitForAppServer(appInfo);

		return appInfo;
	}

	/**
	 * If an application server is not reachable this method is used to report
	 * it as stale
	 * 
	 * @param appId
	 */
	public void reportStaleApp(String appId)
	{
		AppInfo appInfo = registry.getAppInfo(appId);
		if (appInfo != null)
			appInfo.setStatus(AppState.STALE);
	}

	/**
	 * Blocks until the application server is ready
	 * 
	 * @param appInfo
	 */
	private void waitForAppServer(AppInfo appInfo)
	{
		// The monitor contains the thread synchronization
		monitor.waitForApplication(appInfo);
	}

	private void startApp(AppInfo appInfo)
	{
		appInfo.setStatus(AppState.STARTING);
		
		// Enqueue the process start
		monitor.startProcess(appInfo);

		// Wait until the AppServer is online
		waitForAppServer(appInfo);
	}

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}
}
