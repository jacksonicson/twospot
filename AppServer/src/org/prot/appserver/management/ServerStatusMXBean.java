package org.prot.appserver.management;

import javax.management.MXBean;

@MXBean
public interface ServerStatusMXBean
{
	public boolean getState();
	
	public int getRequests();
	
	public String getName();
}
