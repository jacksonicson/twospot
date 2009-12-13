package org.prot.controller.services.controller;

import org.apache.log4j.Logger;
import org.prot.controller.manager.AppManager;

public class ControllerServiceImpl implements ControllerService
{
	private static final Logger logger = Logger.getLogger(ControllerServiceImpl.class);

	private AppManager manager;

	@Override
	public void updateApp(String appId)
	{
		manager.reportStaleApp(appId);
	}

	public void setManager(AppManager manager)
	{
		this.manager = manager;
	}
}
