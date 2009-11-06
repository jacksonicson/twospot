package org.prot.manager.service.frontend;

import org.apache.log4j.Logger;
import org.prot.manager.exceptions.MissingControllerException;
import org.prot.manager.pojos.AppServerInfo;

public class FrontendServiceImpl implements FrontendService
{
	private static final Logger logger = Logger.getLogger(FrontendServiceImpl.class); 
	
	@Override
	public AppServerInfo chooseAppServer(String appId) throws MissingControllerException
	{
		logger.debug("chooseAppServer"); 
		
		AppServerInfo info = new AppServerInfo(); 
		info.setControllerAddress("127.0.0.1:8080");
		info.setControllerPort(8080); 
		
		return info;
	}

}
