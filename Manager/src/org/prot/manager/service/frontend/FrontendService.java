package org.prot.manager.service.frontend;

import org.prot.manager.pojos.AppServerInfo;

public interface FrontendService
{
	public AppServerInfo chooseAppServer(String appId);
}
