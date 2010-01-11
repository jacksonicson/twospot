package org.prot.controller.app;

import org.prot.controller.stats.AppRequestStats;
import org.prot.util.managment.gen.ManagementData;

public class AppManagement
{
	private final AppRequestStats stats = new AppRequestStats();

	private ManagementData.AppServer appServer;

	public void update(ManagementData.AppServer appServer)
	{
		this.appServer = appServer;
	}

	public ManagementData.AppServer getAppServer()
	{
		return this.appServer;
	}

	public AppRequestStats getStats()
	{
		return this.stats;
	}
}
