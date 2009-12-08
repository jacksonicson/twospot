package org.prot.controller.manager;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class AppManager
{
	private static final Logger logger = Logger.getLogger(AppManager.class);

	private static final long MAINTENANCE_TIME = 5000;

	private ThreadPool threadPool;

	private AppRegistry registry;

	private AppMonitor monitor;

	public void init()
	{
		// Create AppMonitor and AppReigstry
		monitor = new AppMonitor(threadPool);
		registry = new AppRegistry();

		// Schedule the maintaince task
		Scheduler.addTask(new MaintenanceTask());
	}

	private enum Todo
	{
		START, WAIT
	};

	public AppInfo requireApp(String appId)
	{
		logger.debug("Required AppId: " + appId);
		
		// Get or register the AppServer
		AppInfo appInfo = registry.getOrRegisterApp(appId);

		// Todo-Information
		Todo todo = null;

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

	public boolean checkToken(String token)
	{
		// return true;

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

		// Wait until the AppServer is online
		return monitor.waitForApplication(appInfo);
	}

	private void doMaintenance()
	{
		// Find and kill all idle AppServers
		Set<AppInfo> killed = registry.tick();
		if (killed != null)
			monitor.killProcess(killed);
	}

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
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
			logger.debug("Controller does maintenance work");
			doMaintenance();
		}
	}
}
