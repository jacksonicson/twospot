package org.prot.app.services.platform;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.deploy.DeployService;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public final class PlatformServiceFactory
{
	private static final Logger logger = Logger.getLogger(PlatformServiceFactory.class);

	private static final String CONTROLLER_ADDRESS = "localhost";

	private static PlatformService platformService;

	private static final int getRmiPort()
	{
		return Configuration.getInstance().getRmiRegistryPort();
	}

	private static PlatformService createPlatformService()
	{
		DeployService deployService = AccessController.doPrivileged(new PrivilegedAction<DeployService>()
		{
			public DeployService run()
			{
				RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
				proxyFactory.setServiceInterface(DeployService.class);
				proxyFactory.setServiceUrl("rmi://" + CONTROLLER_ADDRESS + ":" + getRmiPort()
						+ "/appserver/DeployService");
				proxyFactory.afterPropertiesSet();

				Object object = proxyFactory.getObject();
				if (object == null)
				{
					logger.error("Could not connect with the DeployService");
					throw new NullPointerException();
				}

				return (DeployService) object;
			}
		});

		return new PlatformService(deployService);
	}

	private static PlatformService createMockPlatformService()
	{
		return new PlatformService(new MockDeployService());
	}

	public static PlatformService getPlatformService()
	{
		if (platformService == null)
		{
			switch (Configuration.getInstance().getServerMode())
			{
			case DEVELOPMENT:
				platformService = createMockPlatformService();
				break;
			case SERVER:
				platformService = createPlatformService();
				break;
			}
		}

		return platformService;
	}
}
