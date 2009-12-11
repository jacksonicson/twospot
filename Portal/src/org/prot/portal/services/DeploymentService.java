package org.prot.portal.services;

import org.apache.log4j.Logger;
import org.prot.app.services.platform.PlatformService;
import org.prot.app.services.platform.PlatformServiceFactory;

public class DeploymentService
{
	private static final Logger logger = Logger.getLogger(DeploymentService.class);

	public String announceDeployment(String appId, String version)
	{
		PlatformService platformService = PlatformServiceFactory.getPlatformService();
		return platformService.announceApp(appId, version);
	}

	public void deployApplication(String appId, String version)
	{
		PlatformService platformService = PlatformServiceFactory.getPlatformService();
		platformService.appDeployed(appId, version);
	}
}
