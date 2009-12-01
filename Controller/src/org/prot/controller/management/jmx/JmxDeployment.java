package org.prot.controller.management.jmx;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.management.AppServerWatcher;

public class JmxDeployment implements IJmxDeployment
{
	private static final Logger logger = Logger.getLogger(JmxDeployment.class);

	private AppServerWatcher management;

	@Override
	public String getName()
	{
		return "JmxDeployment";
	}

	@Override
	public Set<String> fetchDeployedApps()
	{
		Set<String> apps = new HashSet<String>();
		apps.addAll(management.fetchDeployedApps());
		return apps;
	}

	public void setManagement(AppServerWatcher management)
	{
		this.management = management;
	}

	@Override
	public void notifyDeployment(Set<String> appIds)
	{

	}
}
