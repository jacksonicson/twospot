package org.prot.controller.management.jmx;

import java.util.List;

public interface IJmxDeployment
{
	public List<String> getDeployedApps();
	
	public String getName();
}
