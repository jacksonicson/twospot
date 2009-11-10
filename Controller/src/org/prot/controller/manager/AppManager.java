package org.prot.controller.manager;

import org.prot.controller.manager.exceptions.AppServerFailedException;

public class AppManager
{
	private AppRegistry registry;

	private AppMonitor monitor;

	public AppManager()
	{
		// Initialize
		monitor = new AppMonitor();
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

		// Update the interaction time
		appInfo.setLastInteraction(System.currentTimeMillis()); 
		
		// Update status (this section is not synchronized and therefore the
		// decisions made here could be wrong!
		switch (appInfo.getStatus())
		{
		case ONLINE:
			return appInfo;
		case STARTING:
			waitForAppServer(appInfo);
			break;
		case FAILED:
			throw new AppServerFailedException();
		case OFFLINE:
		case STALE:
			appInfo.setStatus(AppState.STARTING);
			startApp(appInfo);
			break;
		}

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
		// Get a process for this application
		AppProcess process = monitor.getProcess(appInfo);
		
		// Enqueue the process start 
		monitor.startProcess(process);
		
		// Wait until the AppServer is online
		waitForAppServer(appInfo);
	}
}
