package org.prot.appserver.management;

public class ServerStatus implements ServerStatusMXBean
{
	public boolean getState()
	{
		return false;
	}

	private int requests = 0;
	
	public int getRequests()
	{
		return this.requests; 
	}
	
	public String getName()
	{
		return "MxServerStatus";
	}
	
	public void incrementRequests()
	{
		this.requests++; 
	}
}
