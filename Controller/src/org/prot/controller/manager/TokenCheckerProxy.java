package org.prot.controller.manager;

public class TokenCheckerProxy implements TokenChecker
{
	private AppManager appManager;

	@Override
	public boolean checkToken(String token)
	{
		return appManager.checkToken(token);
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}
}
