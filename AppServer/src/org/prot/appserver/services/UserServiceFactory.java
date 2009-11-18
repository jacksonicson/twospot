package org.prot.appserver.services;

import org.prot.controller.services.user.PrivilegedUserService;
import org.prot.controller.services.user.UserService;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class UserServiceFactory
{
	private static PrivilegedUserService userService;

	public static UserService getUserService()
	{
		if (userService == null)
		{
			RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
			proxyFactory.setServiceInterface(PrivilegedUserService.class);
			proxyFactory.setServiceUrl("rmi://localhost:2299/appserver/UserService");
			proxyFactory.afterPropertiesSet();
			userService = (PrivilegedUserService) proxyFactory.getObject();
		}

		return userService;
	}
}
