package org.prot.appserver.management;

public class ServerStatus implements ServerStatusMXBean
{
	public boolean getState() {
		return false;  
	}
	
	public String getName() {
		return "MxServerStatus"; 
	}
}
