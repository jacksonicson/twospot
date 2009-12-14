package org.prot.controller.management.services;

import java.util.Set;

public interface IJmxDeployment
{
	public String[] fetchDeployedApps();

	public void notifyDeployment(Set<String> appIds);

	public String getName();
}
