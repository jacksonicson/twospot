package org.prot.controller.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.prot.controller.manager.AppManager;
import org.prot.controller.manager.appserver.IAppServerStats;

public class AppServerWatcher
{
	private static final Logger logger = Logger.getLogger(AppServerWatcher.class);

	private Timer timer = new Timer(true);

	private AppManager manager;

	private Map<String, PerformanceData> performanceData = new HashMap<String, PerformanceData>();

	public void init()
	{
		this.timer.scheduleAtFixedRate(new Watcher(), 0, 5000);
	}

	private final PerformanceData getOrCreatePerformanceData(String appId)
	{
		PerformanceData data = performanceData.get(appId);
		if (data == null)
		{
			data = new PerformanceData(appId);
			performanceData.put(appId, data);
		}

		return data;
	}

	public void notifyDeployment(String appId)
	{

	}

	public List<String> getDeployedApps()
	{
		return new ArrayList<String>();
	}

	public List<String> getRunningApps()
	{
		return new ArrayList<String>();
	}

	public long getRps()
	{
		return 0;
	}

	private void updateManagementData()
	{
		Set<String> appIds = manager.getAppIds();
		for (String appId : appIds)
		{
			IAppServerStats stats = connectWithApp(appId);
			if (stats == null)
				continue;

			updateApp(appId, stats);
		}
	}

	private void updateApp(String appId, IAppServerStats stats)
	{
		PerformanceData perfData = performanceData.get(appId);
		if (perfData == null)
		{
			perfData = new PerformanceData(appId);
			performanceData.put(appId, perfData);
		}

		long rps = stats.getRequestsPerSecond();
		logger.info("RPS: " + rps);
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

	class Watcher extends TimerTask
	{
		@Override
		public void run()
		{
			updateManagementData();
		}
	}

	public void setManager(AppManager manager)
	{
		this.manager = manager;
	}
}
