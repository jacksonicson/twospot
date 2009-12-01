package org.prot.controller.management.jmx;

import java.util.Set;

public interface IJmxResources
{
	public long runningAppServers();
	
	public long loadAverage();
	
	public long requestsPerSecond();

	public Set<String> getApps();
	
	public String getName();
}
