package org.prot.app.services.platform;

import org.prot.app.services.PrivilegedServiceException;
import org.prot.controller.services.deploy.DeployService;

public class MockDeployService implements DeployService
{
	@Override
	public void appDeployed(String token, String appId, String version)
	{
		throw new PrivilegedServiceException();
	}

	@Override
	public String announceDeploy(String arg0, String arg1, String arg2)
	{
		throw new PrivilegedServiceException();
	}

	@Override
	public void register(String arg0, String arg1, String arg2)
	{
		throw new PrivilegedServiceException();
	}
}
