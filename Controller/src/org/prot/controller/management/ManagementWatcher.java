package org.prot.controller.management;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.prot.controller.manager.AppManager;
import org.prot.controller.manager.appserver.IAppServerStats;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class ManagementWatcher
{
	private static final Logger logger = Logger.getLogger(ManagementWatcher.class);

	private Timer timer = new Timer(true);

	private AppManager manager;

	private Map<String, PerformanceData> performanceData = new HashMap<String, PerformanceData>();

	public void init()
	{
		this.timer.scheduleAtFixedRate(new Watcher(), 0, 5000);
	}

	public void notifyDeployment(String appId)
	{
		
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
		try
		{
			RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();

			proxyFactory.setServiceInterface(IAppServerStats.class);
			proxyFactory.setServiceUrl("rmi://" + "localhost:2299" + "/appserver/" + appId);

			proxyFactory.afterPropertiesSet();

			IAppServerStats stats = (IAppServerStats) proxyFactory.getObject();
			return stats;

		} catch (Exception e)
		{
			// Don't handle this
			logger.debug("Could not connect to the AppServer management");
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
