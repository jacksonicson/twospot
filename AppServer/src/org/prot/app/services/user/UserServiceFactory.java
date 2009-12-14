package org.prot.app.services.user;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class UserServiceFactory
{
	private static final Logger logger = Logger.getLogger(UserServiceFactory.class);

	private static final String CONTROLLER_ADDRESS = "localhost";

	private static UserService userService;

	private static final int getRmiPort()
	{
		return Configuration.getInstance().getRmiRegistryPort();
	}

	private static UserService createUserService()
	{
		UserService userService = AccessController.doPrivileged(new PrivilegedAction<UserService>()
		{
			public UserService run()
			{
				RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
				proxyFactory.setServiceInterface(org.prot.controller.services.user.UserService.class);
				proxyFactory.setServiceUrl("rmi://" + CONTROLLER_ADDRESS + ":" + getRmiPort()
						+ "/appserver/UserService");
				proxyFactory.afterPropertiesSet();

				Object object = proxyFactory.getObject();
				if (object == null)
				{
					logger.error("Could not connect with the UserService");
					throw new NullPointerException();
				}

				UserServiceImpl userService = new UserServiceImpl(
						(org.prot.controller.services.user.UserService) object);

				return userService;
			}

		});

		return userService;
	}

	private static UserService createMockUserService()
	{
		return new MockUserService();
	}

	public static UserService getUserService()
	{
		if (userService == null)
		{
			logger.debug("Creating new UserService");

			switch (Configuration.getInstance().getServerMode())
			{
			case DEVELOPMENT:
				userService = createMockUserService();
				break;
			case SERVER:
				userService = createUserService();
				break;
			}

		}

		return userService;
	}
}
