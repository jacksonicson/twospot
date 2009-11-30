package org.prot.controller.manager.appserver;


public interface IAppServerStats
{
	public long getRequestsPerSecond(); 
	
	public String getName();
}
