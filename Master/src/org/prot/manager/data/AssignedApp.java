package org.prot.manager.data;

public class AssignedApp
{
	private final String appId;

	private final long timestamp;

	public AssignedApp(String appId)
	{
		this.appId = appId;
		this.timestamp = System.currentTimeMillis();
	}

	public String getAppId()
	{
		return this.appId;
	}

	boolean isOld()
	{
		long diff = System.currentTimeMillis() - timestamp;
		return diff > 20000;
	}
}
