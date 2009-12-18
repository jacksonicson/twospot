package org.prot.controller.stats;

import org.prot.controller.app.AppRegistry;

public class Stats
{
	private AppRegistry registry;

	public void handle(final String appId)
	{
		AppRequestStats stats = registry.getAppInfo(appId).getAppManagement().getAppRequestStats();
		stats.handle();
	}

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}
}
