package org.prot.manager.stats;

public class AssignedApp
{
	// AppId
	private final String appId;

	// Timestamp of assignment (Assignments time out!)
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
