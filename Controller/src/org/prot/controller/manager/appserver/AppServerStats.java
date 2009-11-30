package org.prot.controller.manager.appserver;

import org.prot.util.managment.RequestStats;

public class AppServerStats implements IAppServerStats
{
	private static final String NAME = "AppServerStats";
	
	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public long getRequestsPerSecond()
	{
		return RequestStats.getRps();
	}

}
