package org.prot.manager.data;

import java.util.Set;

public class ManagementData
{
	private long rps;

	private Set<String> runningApps;

	public long getRps()
	{
		return rps;
	}

	public void setRps(long rps)
	{
		this.rps = rps;
	}

	public Set<String> getRunningApps()
	{
		return runningApps;
	}

	public void setRunningApps(Set<String> runningApps)
	{
		this.runningApps = runningApps;
	}

}
