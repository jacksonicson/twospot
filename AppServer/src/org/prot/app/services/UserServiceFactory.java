package org.prot.app.services;

import java.security.AccessController;
import java.security.PrivilegedAction;

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
			Object o = AccessController.doPrivileged(new PrivilegedAction()
			{
				public Object run()
				{

					System.out.println("class codesource"
							+ UserServiceFactory.class.getProtectionDomain().getCodeSource());

					System.out.println("class codesource"
							+ this.getClass().getProtectionDomain().getCodeSource());

					RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
					proxyFactory.setServiceInterface(org.prot.controller.services.user.UserService.class);
					proxyFactory.setServiceUrl("rmi://localhost:2299/appserver/UserService");
					proxyFactory.afterPropertiesSet();

					Object object = proxyFactory.getObject();
					if (object == null)
					{
						logger.error("Could not connect with the UserService");
						throw new NullPointerException();
					}

					UserService userService = new UserService(
							(org.prot.controller.services.user.UserService) object);

					// Object o = userService;
					return userService;
				}

			});

			userService = (UserService) o;
		}

		return userService;
	}
}
