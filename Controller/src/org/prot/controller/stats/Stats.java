package org.prot.controller.stats;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Stats
{
	private Map<String, AppRequestStats> stats = new ConcurrentHashMap<String, AppRequestStats>();

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
}
