package org.prot.controller.services.deploy;

import org.prot.controller.services.PrivilegedAppServer;

public interface DeployService
{
	@PrivilegedAppServer
	public void appDeployed(String token, String appId, String version);
}
