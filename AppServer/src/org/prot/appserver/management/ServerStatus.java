package org.prot.appserver.management;

public class ServerStatus implements ServerStatusMXBean
{
	public int getValue() {
		return 100; 
	}
	
	public String getName() {
		return "MxServerStatus"; 
	}
}
