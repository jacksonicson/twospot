package org.prot.app.services.platform;

import org.prot.app.services.PrivilegedServiceException;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.deploy.DeployService;

public final class PlatformService
{
	private final DeployService deployService;

	PlatformService(DeployService deployService)
	{
		this.deployService = deployService;
	}

	public String announceApp(String appId, String version)
	{
		final String token = Configuration.getInstance().getAuthenticationToken();
		if (token == null)
			throw new PrivilegedServiceException();

		return deployService.announceDeploy(token, appId, version);
	}

	public void appDeployed(String appId, String version)
	{
		final String token = Configuration.getInstance().getAuthenticationToken();
		if (token == null)
			throw new PrivilegedServiceException();

		deployService.appDeployed(token, appId, version);
	}

	public void register(String appId, String version)
	{
		final String token = Configuration.getInstance().getAuthenticationToken();
		if (token == null)
			throw new PrivilegedServiceException();

		deployService.register(token, appId, version);
	}
}
