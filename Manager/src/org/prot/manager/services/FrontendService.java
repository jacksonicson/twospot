package org.prot.manager.services;

import org.prot.manager.config.ControllerInfo;
import org.prot.manager.exceptions.MissingControllerException;

public interface FrontendService
{
	public ControllerInfo chooseAppServer(String appId) throws MissingControllerException;
	
	public void newAppOrVersion(String appId); 
}
