package org.prot.controller.manager2;

public class AppManager
{
	private AppRegistry registry;

	private AppMonitor monitor;

	public AppManager()
	{
		monitor = new AppMonitor();
		registry = new AppRegistry();
	}

	public AppInfo requireApp(String appId) throws DuplicatedAppException, AppServerFailedException
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
		case STARTING:
			waitForAppServer(appInfo);
		case ONLINE:
			return appInfo;
		case FAILED:
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
	
	public void staleApp(String appId) {
		System.out.println("Stale app not implemented"); 
	}

	private void waitForAppServer(AppInfo appInfo)
	{
		System.out.println("Wait for app not implemented"); 
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
	}
}
