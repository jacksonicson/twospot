package org.prot.manager.data;

import java.util.List;

public class ManagementData
{
	private long rps; 
	
	private List<String> runningApps;

	public long getRps()
	{
		return rps;
	}

	public void setRps(long rps)
	{
		this.rps = rps;
	}

	public List<String> getRunningApps()
	{
		return runningApps;
	}

	public void setRunningApps(List<String> runningApps)
	{
		this.runningApps = runningApps;
	}
	
}
