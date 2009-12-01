package org.prot.controller.management.jmx;

import java.util.List;
import java.util.Set;

public interface IJmxDeployment
{
	public List<String> getDeployedApps();

	public void notifyDeployment(Set<String> appIds);
	
	public String getName();
}
