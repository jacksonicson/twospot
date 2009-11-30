package org.prot.controller.management;

import java.util.List;

public interface IResources
{
	public long runningAppServers();
	
	public long loadAverage();
	
	public long requestsPerMinute();

	public List<String> getApps();
	
	public String getName();
}
