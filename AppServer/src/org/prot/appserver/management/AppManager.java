package org.prot.appserver.management;

import org.apache.log4j.Logger;

public class AppManager implements IAppServerStats
{
	private static final Logger logger = Logger.getLogger(AppManager.class);
	
	private AppManagement managedApp = new MockManagement();

	public void manage(AppManagement managedApp)
	{
		this.managedApp = managedApp;
	}

	@Override
	public double getRequestsPerSecond()
	{
		return managedApp.requestsPerSecond();
	}

	@Override
	public long averageRequestTime()
	{
		return managedApp.averageRequestTime();
	}

	@Override
	public double ping()
	{
		return managedApp.ping();
	}
}
