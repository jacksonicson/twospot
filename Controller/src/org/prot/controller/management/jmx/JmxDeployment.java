package org.prot.controller.management.jmx;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.controller.management.ManagementWatcher;

public class JmxDeployment implements IJmxDeployment
{
	private static final Logger logger = Logger.getLogger(JmxDeployment.class);

	private ManagementWatcher management;

	@Override
	public String getName()
	{
		return "JmxDeployment";
	}

	@Override
	public List<String> getDeployedApps()
	{
		return management.getDeployedApps();
	}

	public void setManagement(ManagementWatcher management)
	{
		this.management = management;
	}
}
