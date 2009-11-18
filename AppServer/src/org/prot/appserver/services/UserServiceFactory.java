package org.prot.appserver.services;

import org.prot.controller.services.user.PrivilegedUserService;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class UserServiceFactory
{
	private static UserServiceProxy userService;

	public static UserServiceProxy getUserService()
	{
		if (userService == null)
		{
			RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
			proxyFactory.setServiceInterface(PrivilegedUserService.class);
			proxyFactory.setServiceUrl("rmi://localhost:2299/appserver/UserService");
			proxyFactory.afterPropertiesSet();
			
			userService = new UserServiceProxy((PrivilegedUserService)proxyFactory.getObject());
		}

		return userService;
	}
}
