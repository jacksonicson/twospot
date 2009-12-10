package org.prot.manager.data;

import java.util.Set;

public class ManagementData
{
	// Requests per second which are processed by the Controller
	private long rps;

	// Memory consumed by the AppServer's
	private double memLoad; 
	
	// Average CPU usage
	private double averageCpu; 
	
	// Data transfered by the Controller (in and out) (bytes per second)
	private long networkTraffic;
	
	// AppId's which are running under the Controller
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
