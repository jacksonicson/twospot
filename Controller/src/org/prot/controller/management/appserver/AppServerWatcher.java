package org.prot.controller.management.appserver;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.prot.appserver.management.IAppServerStats;
import org.prot.controller.manager.AppManager;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class AppServerWatcher
{
	private static final Logger logger = Logger.getLogger(AppServerWatcher.class);

	private AppManager manager;

	private Map<String, PerformanceData> performanceData = new HashMap<String, PerformanceData>();

	private Map<String, IAppServerStats> connections = new HashMap<String, IAppServerStats>();

	public void init()
	{
		Scheduler.addTask(new Watcher());
	}

	public String[] getRunningApps()
	{
		return (String[]) manager.getAppIds().toArray(new String[0]);
	}

	private PerformanceData getPerformanceData(String appId)
	{
		PerformanceData data = performanceData.get(appId);
		if (data == null)
		{
			data = new PerformanceData(appId);
			performanceData.put(appId, data);
		}

		return data;
	}

	public PerformanceData[] getAppsPerformance()
	{
		PerformanceData[] performanceData = (PerformanceData[]) (this.performanceData.values()
				.toArray(new PerformanceData[0]));
		return performanceData;
	}

	public void remove(String appId)
	{
		performanceData.remove(appId);
		connections.remove(appId);
	}

	private void updateManagementData()
	{
		for (String appId : manager.getAppIds())
		{
			IAppServerStats remObject = getRemoteObject(appId);
			if (remObject == null)
				continue;

			PerformanceData perfData = getPerformanceData(appId);
			if (perfData == null)
				continue;

			try
			{
				updateApp(perfData, remObject);
			} catch (NullPointerException e)
			{
				remove(appId);
			}
		}
	}

	private void updateApp(PerformanceData perfData, IAppServerStats remoteObject)
	{
		perfData.setRequestsPerSecond(remoteObject.getRequestsPerSecond());
		perfData.setAverageRequestTime(remoteObject.averageRequestTime());
		perfData.setLoad(remoteObject.ping());
	}

	private IAppServerStats getRemoteObject(String appId)
	{
		IAppServerStats remoteObject = connections.get(appId);
		if (remoteObject != null)
			return remoteObject;

		try
		{
			Object object = ExceptionSafeProxy.newInstance(getClass().getClassLoader(),
					IAppServerStats.class, appId);
			IAppServerStats connection = (IAppServerStats) object;

			connections.put(appId, connection);

			return connection;

		} catch (Exception e)
		{
			logger.debug("Could not connect with the AppServer management", e);
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
