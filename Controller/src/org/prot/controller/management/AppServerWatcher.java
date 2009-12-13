package org.prot.controller.management;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.manager.AppManager;
import org.prot.controller.manager.appserver.IAppServerStats;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class AppServerWatcher
{
	private static final Logger logger = Logger.getLogger(AppServerWatcher.class);

	private AppManager manager;

	private Map<String, PerformanceData> performanceData = new HashMap<String, PerformanceData>();

	private Set<String> deployed = new HashSet<String>();

	public void init()
	{
		Scheduler.addTask(new Watcher());
	}

	private PerformanceData getOrCreatePerformanceData(String appId)
	{
		PerformanceData data = performanceData.get(appId);
		if (data == null)
		{
			logger.debug("Creating new performance data object");
			data = new PerformanceData(appId);
			performanceData.put(appId, data);
		}

		return data;
	}

	public void notifyDeployment(String appId)
	{
		deployed.add(appId);
	}

	public Set<String> fetchDeployedApps()
	{
		Set<String> copy = new HashSet<String>();
		copy.addAll(deployed);
		deployed.clear();

		return copy;
	}

	public Set<String> getRunningApps()
	{
		return manager.getAppIds();
	}

	public Set<PerformanceData> getAppsPerformance()
	{
		Set<PerformanceData> appsPerformance = new HashSet<PerformanceData>();
		appsPerformance.addAll(performanceData.values());
		return appsPerformance;
	}

	private void updateManagementData()
	{
		Set<String> appIds = manager.getAppIds();
		for (String appId : appIds)
		{
			try
			{
				IAppServerStats stats = connectWithApp(appId);
				if (stats == null)
					continue;

				updateApp(appId, stats);
			} catch (Exception e)
			{
				continue;
			}
		}
	}

	private void updateApp(String appId, IAppServerStats stats)
	{
		PerformanceData perfData = getOrCreatePerformanceData(appId);
		perfData.setRps(stats.getRequestsPerSecond());
	}

	private IAppServerStats connectWithApp(String appId)
	{
		// Check if the connection already exists
		PerformanceData data = getOrCreatePerformanceData(appId);

		if (data != null)
		{
			IAppServerStats connection = data.getConnection();
			if (connection != null)
				return connection;
		}

		// Connection does not exist - create a new proxy
		try
		{
			logger.info("Connecting with the AppServer's management interface");

			Object o = ExceptionSafeProxy.newInstance(getClass().getClassLoader(), IAppServerStats.class,
					appId);
			IAppServerStats connection = (IAppServerStats) o;
			data.setConnection(connection);

			return connection;

		} catch (Exception e)
		{
			logger.debug("error", e);
		}

		return null;
	}

	class Watcher extends SchedulerTask
	{
		@Override
		public void run()
		{
			updateManagementData();
		}

		@Override
		public long getInterval()
		{
			return 5000;
		}
	}

	public void setManager(AppManager manager)
	{
		this.manager = manager;
	}
}
