package org.prot.appserver.management;

import org.prot.controller.manager.appserver.IAppServerStats;

public class AppManager implements IAppServerStats
{
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
}
