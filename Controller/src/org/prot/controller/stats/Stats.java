package org.prot.controller.stats;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.prot.controller.app.AppManager;

public class Stats
{
	private Map<String, AppRequestStats> stats = new ConcurrentHashMap<String, AppRequestStats>();

	private AppManager appManager;

	public void init()
	{
	}

	public void handle(final String appId)
	{
		AppRequestStats stats = this.stats.get(appId);
		if (stats != null)
			stats.handle();
		else
			this.stats.put(appId, new AppRequestStats());
	}

	public double getRps(final String appId)
	{
		AppRequestStats stats = this.stats.get(appId);
		if (stats != null)
			return stats.getRps();

		return -1;
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}
}
