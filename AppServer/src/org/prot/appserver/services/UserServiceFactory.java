package org.prot.appserver.services;

import org.prot.controller.services.UserService;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class UserServiceFactory
{
	private static UserService userService;

	public static UserService getUserService()
	{
		if (userService == null)
		{
			RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
			proxyFactory.setServiceInterface(UserService.class);
			proxyFactory.setServiceUrl("rmi://localhost:2299/UserService");
			proxyFactory.afterPropertiesSet();
			userService = (UserService) proxyFactory.getObject();
		}
		
		return userService; 
	}
}
