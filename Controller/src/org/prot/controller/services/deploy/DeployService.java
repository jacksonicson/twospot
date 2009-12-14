package org.prot.controller.services.deploy;

import org.prot.controller.services.PrivilegedAppServer;

public interface DeployService
{
	@PrivilegedAppServer
	public void register(String token, String appId, String version);

	@PrivilegedAppServer
	public String announceDeploy(String token, String appId, String version);

	@PrivilegedAppServer
	public void appDeployed(String token, String appId, String version);
}
