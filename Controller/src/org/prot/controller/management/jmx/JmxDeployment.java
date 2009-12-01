package org.prot.controller.management.jmx;

import org.apache.log4j.Logger;

public class JmxDeployment implements IJmxDeployment
{
	private static final Logger logger = Logger.getLogger(JmxDeployment.class);

	@Override
	public void deployed(String appId)
	{
		logger.info("deploying: " + appId);
		// TODO: Kill AppServers running this id!
	}

	@Override
	public String getName()
	{
		return "JmxDeployment";
	}

}
