package org.prot.controller.management;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.manager.AppManager;
import org.prot.util.managment.Ping;
import org.prot.util.stats.StatsValue;

public class AppServerWatcher
{
	private static final Logger logger = Logger.getLogger(AppServerWatcher.class);

	private AppManager manager;

	private Map<String, Ping> connections = new HashMap<String, Ping>();

	private Map<String, Set<StatsValue>> data = new HashMap<String, Set<StatsValue>>();

	public Map<String, Set<StatsValue>> getData()
	{
		return data;
	}

	private Ping connectPing(String appId)
	{
		Ping ping = connections.get(appId);
		if (ping != null)
			return ping;

		Ping connection = (Ping) ExceptionSafeProxy.newInstance(getClass().getClassLoader(), Ping.class,
				appId);
		connections.put(appId, connection);

		return connection;
	}

	private void updateData(String appId, Ping ping)
	{
		Set<StatsValue> newData = ping.ping();
		data.put(appId, newData);
	}

	public void update()
	{
		// Delete all old AppServers
		Set<String> appIds = manager.getAppIds();
		for (Iterator<String> it = connections.keySet().iterator(); it.hasNext();)
		{
			String appId = it.next();
			if (!appIds.contains(appId))
			{
				data.remove(appId);
				it.remove();
			}
		}

		// Iterate over all AppIds and poll them
		for (String appId : connections.keySet())
		{
			Ping ping = connectPing(appId);

			try
			{
				updateData(appId, ping);
			} catch (Exception e)
			{
				data.remove(appId);
				connections.remove(appId);
			}
		}
	}

	public void setManager(AppManager manager)
	{
		this.manager = manager;
	}
}
