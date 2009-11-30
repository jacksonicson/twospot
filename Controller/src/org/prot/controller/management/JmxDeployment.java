package org.prot.controller.management;

import org.apache.log4j.Logger;

public class JmxDeployment implements IJmxDeployment
{
	private static final Logger logger = Logger.getLogger(JmxDeployment.class);

	@Override
	public void deployed(String appId)
	{
		logger.info("deploying: " + appId);
	}

	@Override
	public String getName()
	{
		return "JmxDeployment";
	}

}
