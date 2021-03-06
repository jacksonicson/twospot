/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.controller.app;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.controller.zookeeper.DeploymentListener;
import org.prot.controller.zookeeper.SynchronizationService;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;
import org.prot.util.zookeeper.SynchronizationListener;
import org.prot.util.zookeeper.ZooHelper;

public class AppManager implements DeploymentListener, SynchronizationListener
{
	private static final Logger logger = Logger.getLogger(AppManager.class);

	private AppRegistry registry;

	private ProcessWorker processWorker;

	private SynchronizationService managementService;

	private enum Todo
	{
		START, WAIT
	};

	public void init()
	{
		// Schedule the cleanup task which removes unused objects from the
		// registry
		Scheduler.addTask(new CleanupTask());

		// Listener is used to get deployment notifications
		managementService.addDeploymentListener(this);
		managementService.addListener(this);
	}

	public boolean isBlocked(String appId)
	{
		return registry.isBlocked(appId);
	}
	
	public AppInfo requireApp(String appId)
	{
		// Get or register the AppServer
		AppInfo appInfo = registry.getOrRegisterApp(appId);

		// Update last access timestamp (Idle are killed)
		appInfo.touch();
		appInfo.stopRequest();

		// Fast path
		if (appInfo.getStatus() == AppState.ONLINE)
			return appInfo;

		// Todo-Information
		Todo todo = null;

		// Simple state machine for managing the AppServer lifecycle
		synchronized (appInfo)
		{
			// Status may have changed since getting the AppInfo
			AppState state = appInfo.getStatus();
			switch (state)
			{
			case ONLINE:
				// Server is online
				return appInfo;

			case STARTING:
				// Wait until the AppServer is online
				todo = Todo.WAIT;
				break;

			case NEW:
				// Start the AppServer and wait until it is online
				appInfo.setState(AppState.STARTING);
				todo = Todo.START;
				break;

			default:
				// TODO: A solution might be to call getOrRegisterApp(appId) in
				// a loop
				// until we get a working AppServer. This behavior might result
				// in bad requests.
				return appInfo;
			}
		}

		// Is indirectly synchronized by the previous state selection
		switch (todo)
		{
		case START:
			// Start the application. Returns true if a continuation is used
			if (startApp(appInfo))
				return null;

		case WAIT:
			// Waits until the application is ONLINE. Returns true if a
			// continuation is used
			if (processWorker.waitForApplication(appInfo))
				return null;
		}

		return appInfo;
	}

	@Override
	public void deployApp(String appId)
	{
		logger.info("App deployed - changing state for AppId: " + appId);

		// Geht the AppInfo for this application
		AppInfo appInfo = registry.getAppInfo(appId);
		if (appInfo != null)
			appInfo.setState(AppState.DEPLOYED);
		else
			logger.warn("Could not change state");
	}

	@Override
	public synchronized void reconnected(ZooHelper zooHelper)
	{
		logger.info("Reconnecting");

		for (AppInfo appInfo : registry.getDuplicatedAppInfos())
		{
			if (appInfo.getStatus().getLife() == AppLife.SECOND)
			{
				managementService.watchApp(appInfo.getAppId());
				managementService.start(appInfo.getAppId());
			}
		}
	}

	public void staleApp(AppInfo appInfo)
	{
		logger.debug("Reporting stale AppServer");
		
		if (appInfo != null)
			appInfo.setState(AppState.KILLED);
	}

	private boolean startApp(AppInfo appInfo)
	{
		// Enqueue the process start
		processWorker.scheduleStartProcess(appInfo);

		// Watch for application ZooKeeper node for updates
		managementService.watchApp(appInfo.getAppId());

		// Register the application instance within ZooKeeper
		managementService.start(appInfo.getAppId());

		// Wait until the AppServer is online
		return processWorker.waitForApplication(appInfo);
	}

	private synchronized void cleanup()
	{
		// Update appserver stats
		registry.updateStates();

		// Find and kill dead AppServers
		List<AppInfo> dead = registry.killDeadAppInfos();

		// Remove all dead entries
		registry.removeDeadAppInfos();

		// Don't listen on ZooKeeper events any more
		for (AppInfo info : dead)
		{
			// Unregister AppServer from ZooKeeper
			managementService.stop(info.getAppId());
			managementService.removeWatch(info.getAppId());
		}

		// Schedule kill-Tasks for each entry
		if (!dead.isEmpty())
			processWorker.scheduleKillProcess(dead);
	}

	class CleanupTask extends SchedulerTask
	{
		@Override
		public long getInterval()
		{
			return 5000;
		}

		@Override
		public void run()
		{
			try
			{
				cleanup();
			} catch (Exception e)
			{
				logger.error("CleanupTask failed", e);
				System.exit(1);
			}
		}
	}

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}

	public void setManagementService(SynchronizationService managementService)
	{
		this.managementService = managementService;
	}

	public void setProcessWorker(ProcessWorker processWorker)
	{
		this.processWorker = processWorker;
	}
}
