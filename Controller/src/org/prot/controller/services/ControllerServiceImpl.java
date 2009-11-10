package org.prot.controller.services;

public class ControllerServiceImpl implements ControllerService
{
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
		// TODO Auto-generated method stub
		
	}
}
