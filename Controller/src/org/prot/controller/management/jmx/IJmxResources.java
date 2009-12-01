package org.prot.controller.management.jmx;

import java.util.List;

public interface IJmxResources
{
	public long runningAppServers();
	
	public long loadAverage();
	
	public long requestsPerMinute();

	public List<String> getApps();
	
	public String getName();
}
