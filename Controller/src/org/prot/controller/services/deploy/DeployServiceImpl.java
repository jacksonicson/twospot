package org.prot.controller.services.deploy;

import org.apache.log4j.Logger;

public class DeployServiceImpl implements DeployService
{
	private static final Logger logger = Logger.getLogger(DeployServiceImpl.class);
	
	@Override
	public void appDeployed(String token, String appId, String version)
	{
		logger.info("deployed app: " + appId);
	}
}
