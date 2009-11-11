package org.prot.controller.services;

import org.prot.controller.manager.AppManager;

public class ControllerServiceImpl implements ControllerService
{
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateApp(String appId)
	{
		System.out.println("Updating application!!!!!!!!"); 
		manager.reportStaleApp(appId); 
	}

	public void setManager(AppManager manager)
	{
		this.manager = manager;
	}
}
