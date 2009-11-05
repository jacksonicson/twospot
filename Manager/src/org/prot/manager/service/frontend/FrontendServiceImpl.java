package org.prot.manager.service.frontend;

import org.prot.manager.pojos.AppServerInfo;

public class FrontendServiceImpl implements FrontendService
{
	@Override
	public AppServerInfo chooseAppServer(String appId)
	{
		AppServerInfo info = new AppServerInfo(); 
		info.setControllerAddress("127.0.0.1");
		info.setControllerPort(8080); 
		
		return info;
	}

}
