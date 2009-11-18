package org.prot.controller.services;

public class UserServiceImpl implements UserService
{
	@Override
	public void getCurrentUser()
	{
		System.out.println("get current user");
	}

	@Override
	public String getLoginUrl()
	{
		System.out.println("get login url");
		return "http://www.andmedia.de";
	}

}