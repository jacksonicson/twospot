package org.prot.appserver.management;

import javax.management.MXBean;

@MXBean
public interface ServerStatusMXBean
{
	public int getValue();
	
	public String getName();
}
