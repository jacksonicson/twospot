package org.prot.controller.app;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.stats.Stats;
import org.prot.controller.zookeeper.DeploymentListener;
import org.prot.controller.zookeeper.ManagementService;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class AppManager implements DeploymentListener
{
	private static final Logger logger = Logger.getLogger(AppManager.class);

	private static final long MAINTENANCE_TIME = 5000;

	private AppRegistry registry;

	private ProcessWorker processWorker;

	private ManagementService managementService;

	private Stats stats;

	public void init()
	{
		// Schedule the maintaince task
		Scheduler.addTask(new MaintenanceTask());

		// Register listeners
		managementService.addDeploymentListener(this);
	}

	private enum Todo
	{
		START, WAIT
	};

	public AppInfo requireApp(String appId)
	{
		// Get or register the AppServer
		AppInfo appInfo = registry.getOrRegisterApp(appId);

		// Update last access timestamp (Idle are killed)
		appInfo.touch();

		// Todo-Information
		Todo todo = null;

		// This call is not synchronized - most calls end here
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
				// Don't change the state but wait for the server
				todo = Todo.WAIT;
				break;
			case OFFLINE:
			case STALE:
				// Change the state to STARTING and start the server
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
			if (processWorker.waitForApplication(appInfo))
				return null;
			break;
		}

		return appInfo;
	}

	private void killApp(String appId)
	{
		// Geht the AppInfo for this application
		AppInfo appInfo = registry.getAppInfo(appId);

		// Check if the application is available
		if (appInfo == null)
		{
			logger.debug("Cannot kill application, unknown by this Controller - AppId: " + appId);
			return;
		}

		// Update the state
		appInfo.setStatus(AppState.KILLED);
	}

	@Override
	public void deployApp(String appId)
	{
		logger.info("App deployed - killing all AppServer instances of AppId: " + appId);
		killApp(appId);
	}

	public void staleApp(String appId)
	{
		AppInfo appInfo = registry.getAppInfo(appId);

		// Cannot change the state for an unexisting application
		if (appInfo == null)
			return;

		// Update the state
		appInfo.setStatus(AppState.STALE);
	}

	private boolean startApp(AppInfo appInfo)
	{
		// Enqueue the process start
		processWorker.scheduleStartProcess(appInfo);

		// Watch for application updates
		managementService.watchApp(appInfo.getAppId());

		// Register a Listener for the application. If the application is
		// already running it returns true, if not it returns false
		return processWorker.waitForApplication(appInfo);
	}

	private void doMaintenance()
	{
		// Find and kill all idle AppServers
		Set<AppInfo> dead = registry.findDeadApps();

		// If no dead AppServers findDeadApps() returns null
		if (dead != null)
		{
			// Don't listen on ZooKeeper events any more
			for (AppInfo info : dead)
				managementService.removeWatch(info.getAppId());

			// Schedule kill-Tasks for each entry
			processWorker.scheduleKillProcess(dead);
		}

		// Kill everything with low stats
		// TODO: Check if controller is under a high load and kill unused apps
		// in this case!
		// for (String appId : registry.getAppIds())
		// {
		// long time = registry.getAppInfo(appId).getCreationTime();
		// if (System.currentTimeMillis() - time < 60 * 1000)
		// continue;
		//
		// double stat = stats.getRps(appId);
		// if (stat < 0)
		// continue;
		// if (stat < 10)
		// {
		// logger.debug("Try killing app because of bad stats (low rps value): "
		// + stat);
		//
		// // Check ZooKeeper if we are the only control - if we are not
		// // under high load
		// killApp(appId);
		// }
		// }
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

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}

	public void setManagementService(ManagementService managementService)
	{
		this.managementService = managementService;
	}
	
	public void setProcessWorker(ProcessWorker processWorker)
	{
		this.processWorker = processWorker;
	}

	public void setStats(Stats stats)
	{
		this.stats = stats;
	}
}
