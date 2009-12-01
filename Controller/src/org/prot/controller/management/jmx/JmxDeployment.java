package org.prot.controller.management.jmx;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.management.AppServerWatcher;
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
		for (String appId : appIds)
		{
			logger.info("Kill AppId: " + appId + " due to the redeployment"); 
			manager.killApp(appId);
		}
	}

	public void setManager(AppManager manager)
	{
		this.manager = manager;
	}
}
