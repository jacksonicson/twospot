package org.prot.controller.services.controller;

import org.apache.log4j.Logger;
import org.prot.controller.manager.AppManager;

public class ControllerServiceImpl implements ControllerService
{
	private static final Logger logger = Logger.getLogger(ControllerServiceImpl.class);
	
	private AppManager manager; 
	
	@Override
	public int getSystemLoad()
	{
		return (int)(Math.random() * 100);
	}

	@Override
	public void killApp(String appId)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ping()
	{
		// TODO: Return some status informations
	}

	@Override
	public void updateApp(String appId)
	{
		logger.debug("Manager is reporting a stale AppServer: " + appId);
		manager.reportStaleApp(appId); 
	}

	public void setManager(AppManager manager)
	{
		this.manager = manager;
	}
}
