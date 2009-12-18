package org.prot.controller.stats;

import java.util.List;

import org.prot.controller.app.AppRegistry;

public class Stats
{
	private AppRegistry registry;

	private List<BalancingProcessor> processors;

	public void handle(final String appId)
	{
		AppRequestStats stats = registry.getAppInfo(appId).getAppManagement().getAppRequestStats();
		stats.handle();
	}

	public void balance()
	{
		for (BalancingProcessor processor : processors)
		{
			processor.test(null);
		}
	}

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}
}
