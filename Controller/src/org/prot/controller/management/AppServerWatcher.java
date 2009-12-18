package org.prot.controller.management;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppManagement;
import org.prot.controller.app.AppRegistry;
import org.prot.util.managment.Ping;
import org.prot.util.stats.StatsValue;

public class AppServerWatcher
{
	private static final Logger logger = Logger.getLogger(AppServerWatcher.class);

	private AppRegistry registry;

	private Ping connect(String appId)
	{
		Ping connection = (Ping) ExceptionSafeProxy.newInstance(getClass().getClassLoader(), Ping.class,
				appId);
		return connection;
	}

	private void ping(Ping ping, AppManagement management)
	{
		Set<StatsValue> values = ping.ping();
		management.update(values);
	}

	public void update()
	{
		for (String appId : registry.getAppIds())
		{
			AppInfo info = registry.getAppInfo(appId);
			if (info == null)
				continue;

			AppManagement management = info.getAppManagement();
			Ping ping = management.getPing();
			if (ping == null)
			{
				ping = connect(appId);
				management.setPing(ping);
			}

			try
			{
				ping(ping, management);
			} catch (Exception e)
			{
				logger.trace(e);
				management.setPing(null);
			}
		}
	}

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}

}
