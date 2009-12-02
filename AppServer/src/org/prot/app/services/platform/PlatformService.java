package org.prot.app.services.platform;

import org.prot.appserver.config.Configuration;
import org.prot.controller.services.deploy.DeployService;

public final class PlatformService
{
	private final DeployService deployService;

	PlatformService(DeployService deployService)
	{
		this.deployService = deployService;
	}

	public void appDeployed(String appId, String version)
	{
		final String token = Configuration.getInstance().getAuthenticationToken();
		deployService.appDeployed(token, appId, version);
	}
}
