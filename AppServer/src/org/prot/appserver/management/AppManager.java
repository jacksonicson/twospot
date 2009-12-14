package org.prot.appserver.management;

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

	@Override
	public long averageRequestTime()
	{
		return managedApp.averageRequestTime();
	}
}
