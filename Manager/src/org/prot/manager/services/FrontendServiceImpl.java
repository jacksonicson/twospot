package org.prot.manager.services;

import org.apache.log4j.Logger;
import org.prot.manager.config.ControllerInfo;
import org.prot.manager.exceptions.MissingControllerException;

public class FrontendServiceImpl implements FrontendService
{
	private static final Logger logger = Logger.getLogger(FrontendServiceImpl.class);

	@Override
	public ControllerInfo chooseAppServer(String appId) throws MissingControllerException
	{
		logger.info("choosing an app servfer");
		return null;
	}

}
