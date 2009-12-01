package org.prot.controller.management.jmx;

import java.util.List;

import org.prot.controller.management.AppServerWatcher;

public class JmxResources implements IJmxResources
{
	private static final String NAME = "Resources";

	private AppServerWatcher management;

	@Override
	public long loadAverage()
	{
		return 0;
	}

	@Override
	public long requestsPerMinute()
	{
		return management.getRps();
	}

	@Override
	public long runningAppServers()
	{
		return 0;
	}

	@Override
	public List<String> getApps()
	{
		return management.getRunningApps();
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	public void setManagement(AppServerWatcher management)
	{
		this.management = management;
	}
}
