package org.prot.controller.services.deploy;

import org.apache.log4j.Logger;
import org.prot.controller.management.ManagementWatcher;
import org.prot.controller.manager.AppManager;

public class DeployServiceImpl implements DeployService
{
	private static final Logger logger = Logger.getLogger(DeployServiceImpl.class);

	private AppManager appManager;

	private ManagementWatcher management;

	@Override
	public void appDeployed(String token, String appId, String version)
	{
		// Check the token
		if(appManager.checkToken(token) == false)
			return;
		
		// Store the info in the management component
		management.notifyDeployment(appId);
	}

	public void setManagement(ManagementWatcher management)
	{
		this.management = management;
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}
}
