package org.prot.controller.management;

import org.prot.controller.manager.appserver.IAppServerStats;

public class PerformanceData
{
	private IAppServerStats connection;

	private final String appId;

	private long rps;

	public PerformanceData(String appId)
	{
		this.appId = appId;
	}

	public long getRps()
	{
		return rps;
	}

	public void setRps(long rps)
	{
		this.rps = rps;
	}

	public String getAppId()
	{
		return appId;
	}

	IAppServerStats getConnection()
	{
		return connection;
	}

	void setConnection(IAppServerStats connection)
	{
		this.connection = connection;
	}
}
