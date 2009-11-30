package org.prot.controller.management;

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

	public void init()
	{
		this.timer.scheduleAtFixedRate(new Watcher(), 0, 5000);
	}

	private void updateManagementData()
	{
		Set<String> appIds = manager.getAppIds();
		for (String appId : appIds)
		{
			IAppServerStats stats = connectWithApp(appId);
			if (stats == null)
				continue;

			long rps = stats.getRequestsPerSecond();
			logger.info("RPS: " + rps);
		}
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
			logger.debug("Could not connect to the AppServer management", e);
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
