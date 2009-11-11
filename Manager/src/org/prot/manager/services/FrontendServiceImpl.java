package org.prot.manager.services;

import org.apache.log4j.Logger;
import org.prot.manager.config.ControllerInfo;
import org.prot.manager.config.StaticConfiguration;
import org.prot.manager.exceptions.MissingControllerException;

public class FrontendServiceImpl implements FrontendService
{
	private static final Logger logger = Logger.getLogger(FrontendServiceImpl.class);

	private StaticConfiguration configuration = new StaticConfiguration(); 
	
	@Override
	public ControllerInfo chooseAppServer(String appId) throws MissingControllerException
	{
		logger.info("frontend requests an appserver info"); 
		return (ControllerInfo)configuration.getControllers().toArray()[0];
	}

	@Override
	public void newAppOrVersion(String appId)
	{
		logger.info("new app or version: " + appId); 
	}
}
