package org.prot.appserver.management;

import java.util.Set;

import org.prot.util.stats.StatsValue;

public class AppManager implements Ping
{
	private Management managedApp = new MockManagement();

	public void manage(Management managedApp)
	{
		this.managedApp = managedApp;
	}

	@Override
	public Set<StatsValue> ping()
	{
		return managedApp.ping();
	}
}
