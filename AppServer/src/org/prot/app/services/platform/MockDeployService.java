package org.prot.app.services.platform;

import org.prot.controller.services.deploy.DeployService;

public class MockDeployService implements DeployService
{

	@Override
	public void appDeployed(String token, String appId, String version)
	{
		// Do nothing
		return;
	}
}
