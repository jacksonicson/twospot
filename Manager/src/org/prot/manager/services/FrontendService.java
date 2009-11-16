package org.prot.manager.services;

import org.prot.manager.data.ControllerInfo;

public interface FrontendService
{
	/**
	 * @param appId
	 * @return null if there was an error or the controller information
	 * @throws MissingControllerException
	 */
	public ControllerInfo chooseAppServer(String appId);
	
	public void newAppOrVersion(String appId); 
}
