package org.prot.controller.management.jmx;

import java.util.Set;

public interface IJmxDeployment
{
	public Set<String> fetchDeployedApps();

	public void notifyDeployment(Set<String> appIds);

	public String getName();
}
