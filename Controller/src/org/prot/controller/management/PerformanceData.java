package org.prot.controller.management;

public class PerformanceData
{
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
}
