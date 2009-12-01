package org.prot.controller.management.jmx;

import java.util.List;

import org.prot.controller.management.ManagementWatcher;

public class JmxResources implements IJmxResources
{
	private static final String NAME = "Resources";

	private ManagementWatcher management;

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

	public void setManagement(ManagementWatcher management)
	{
		this.management = management;
	}
}
