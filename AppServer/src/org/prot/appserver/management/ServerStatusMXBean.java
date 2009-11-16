package org.prot.appserver.management;

import javax.management.MXBean;

@MXBean
public interface ServerStatusMXBean
{
	public String getName();
	
	public String getAppId();

	public boolean isOnline();

	public int getRequestNumber();

	public long getCpuCycles();

	public long getMemoryUsage();
}
