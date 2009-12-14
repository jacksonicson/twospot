package org.prot.controller.manager;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.zookeeper.DeploymentListener;
import org.prot.controller.zookeeper.ManagementService;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class AppManager implements DeploymentListener
{
	private static final Logger logger = Logger.getLogger(AppManager.class);

	private static final long MAINTENANCE_TIME = 5000;

	private AppRegistry registry;

	private AppMonitor monitor;

	private ManagementService managementService;

	public void init()
	{
		// Schedule the maintaince task
		Scheduler.addTask(new MaintenanceTask());
	}

	private enum Todo
	{
		START, WAIT
	};

	public AppInfo requireApp(String appId)
	{
		// Get or register the AppServer
		AppInfo appInfo = registry.getOrRegisterApp(appId);

		// Todo-Information
		Todo todo = null;

		// Performance
		if (appInfo.getStatus() == AppState.ONLINE)
			return appInfo;

		// Simple state machine for managing the AppServer lifecycle
		synchronized (appInfo)
		{
			// Operations depends on app status
			switch (appInfo.getStatus())
			{
			case ONLINE:
			case FAILED:
				// Don't change the state
				return appInfo;
			case STARTING:
				// Don't change the state
				logger.debug("AppServer is starting, waiting ...");
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

	boolean checkToken(String token)
	{
		logger.debug("Checking token: " + token);

		// False if there is no token
		if (token == null)
		{
			logger.debug("Invalid token - token is null");
			return false;
		}

		// Iterate over all running applications
		for (String appId : registry.getAppIds())
		{
			// Get application infos and the token
			AppInfo info = registry.getAppInfo(appId);

			// Copare stored token
			if (token.equals(info.getProcessToken()))
			{
				// If both tokens are equal - return true
				logger.debug("Valid token");
				return true;
			}
		}

		// No matching token found
		logger.debug("Invalid token - token is unknown");
		return false;
	}

	public Set<String> getAppIds()
	{
		return registry.getAppIds();
	}

	public void killApp(String appId)
	{
		// Geht the AppInfo for this application
		AppInfo appInfo = registry.getAppInfo(appId);

		// Check if the application is running
		if (appInfo == null)
		{
			logger.debug("Cannot kill application " + appId + " - not running");
			return;
		}

		// Update the state
		appInfo.setStatus(AppState.KILLED);
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

		// Watch for application updates
		managementService.addDeploymentListener(this, appInfo.getAppId());

		// Wait until the AppServer is online
		return monitor.waitForApplication(appInfo);
	}

	private void doMaintenance()
	{
		// Find and kill all idle AppServers
		Set<AppInfo> dead = registry.findDeadApps();
		if (dead != null)
			monitor.killProcess(dead);
	}

	class MaintenanceTask extends SchedulerTask
	{
		@Override
		public long getInterval()
		{
			return MAINTENANCE_TIME;
		}

		@Override
		public void run()
		{
			logger.debug("Maintenance");
			doMaintenance();
		}
	}

	@Override
	public void appDeployed(String appId)
	{
		logger.info("App deployed - killing all AppServer instances: " + appId);
		killApp(appId);
	}

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}

	public void setMonitor(AppMonitor monitor)
	{
		this.monitor = monitor;
	}

	public void setManagementService(ManagementService managementService)
	{
		this.managementService = managementService;
	}
}
