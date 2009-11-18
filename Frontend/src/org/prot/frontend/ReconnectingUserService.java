package org.prot.frontend;

import org.apache.log4j.Logger;
import org.prot.manager.services.UserService;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class ReconnectingUserService
{
	private static final Logger logger = Logger.getLogger(Manager.class);

	private UserService userService;

	private boolean connect()
	{
		if (Configuration.get().getManagerAddress() == null)
			return false;

		if (userService == null)
		{
			try
			{
				RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();

				proxyFactory.setServiceInterface(UserService.class);
				proxyFactory.setServiceUrl("rmi://" + Configuration.get().getManagerAddress()
						+ "/userService");

				proxyFactory.afterPropertiesSet();

				userService = (UserService) proxyFactory.getObject();
			} catch (Exception e)
			{
				// Connection failed
				logger.error("Connection failed"); 
				userService = null;
				return false;
			}
		}

		return true;
	}

	public String login(String appId, String username, String md5)
	{
		if (!connect())
			return null;

		try
		{
			return userService.login(appId, username, md5);
		} catch (Exception e)
		{
			logger.error("Connection to the UserService lost");
			userService = null;
		}

		return null;
	}
}
