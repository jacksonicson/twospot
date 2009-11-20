package org.prot.app.services;

import org.apache.log4j.Logger;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class UserServiceFactory
{
	private static final Logger logger = Logger.getLogger(UserServiceFactory.class);

	private static UserService userService;

	public static UserService getUserService()
	{
		if (userService == null)
		{
			RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
			proxyFactory.setServiceInterface(org.prot.controller.services.user.UserService.class);
			proxyFactory.setServiceUrl("rmi://localhost:2299/appserver/UserService");
			proxyFactory.afterPropertiesSet();

			Object object = proxyFactory.getObject();
			if(object == null)
			{
				logger.error("Could not connect with the UserService"); 
				throw new NullPointerException();
			}

			userService = new UserService((org.prot.controller.services.user.UserService) object);
		}

		return userService;
	}
}
