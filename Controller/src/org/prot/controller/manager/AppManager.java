package org.prot.controller.manager;

import org.prot.controller.manager.exceptions.AppServerFailedException;
import org.prot.controller.manager.exceptions.DuplicatedAppException;

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
	// TODO: synchronized weg - bei laufenden prozessen bremst das aus!
	// Alle Prozesse müssen warten wenn ein Server gestartet wird
	public synchronized AppInfo requireApp(String appId) throws DuplicatedAppException,
			AppServerFailedException
	{
		// get app info
		AppInfo appInfo = registry.getAppInfo(appId);

		// register a new app
		if (appInfo == null)
		{
			appInfo = registry.registerApp(appId);
		}

		// action depends on app status
		switch (appInfo.getStatus())
		{
		case ONLINE:
			return appInfo;
		case STARTING:
			waitForAppServer(appInfo);
		case FAILED:
			// TODO: Wait some time or until new app revision
			throw new AppServerFailedException();
		case OFFLINE:
			startApp(appInfo);
			break;
		case STALE:
			restartApp(appInfo);
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
	public synchronized void reportStaleApp(String appId)
	{
		AppInfo appInfo = registry.getAppInfo(appId);
		appInfo.setStatus(AppState.STALE);
	}

	/**
	 * Blocks until the application server is ready 
	 * @param appInfo
	 */
	private void waitForAppServer(AppInfo appInfo)
	{
		AppProcess process = monitor.getProcess(appInfo);
		process.waitForAppServer();
	}

	private void restartApp(AppInfo appInfo)
	{
		startApp(appInfo);
	}

	private void startApp(AppInfo appInfo)
	{
		AppProcess process = monitor.getProcess(appInfo);
		if (process == null)
		{
			process = new AppProcess(appInfo);
			monitor.registerProcess(process);
		}

		process.startOrRestart();
		waitForAppServer(appInfo);
	}
}
