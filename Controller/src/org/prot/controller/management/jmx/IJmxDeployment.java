package org.prot.controller.management.jmx;

public interface IJmxDeployment
{
	public void deployed(String appId);
	
	public String getName();
}
