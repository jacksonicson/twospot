package org.prot.controller.management.services;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.management.appserver.AppServerWatcher;
import org.prot.controller.manager.AppManager;

public class JmxDeployment implements IJmxDeployment
{
	private static final Logger logger = Logger.getLogger(JmxDeployment.class);

	private AppManager manager;

	private AppServerWatcher management;

	@Override
	public String getName()
	{
		return "JmxDeployment";
	}

	@Override
	public String[] fetchDeployedApps()
	{
		return management.fetchDeployedApps();
	}

	@Override
	public void notifyDeployment(Set<String> appIds)
	{
		for (String appId : appIds)
		{
			logger.info("Deploying: " + appId);
			manager.killApp(appId);
		}
	}

	public void setManager(AppManager manager)
	{
		this.manager = manager;
	}

	public void setManagement(AppServerWatcher management)
	{
		this.management = management;
	}
}
