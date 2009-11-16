package org.prot.frontend;

import org.apache.log4j.Logger;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.services.FrontendService;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class Manager implements FrontendService
{
	private static final Logger logger = Logger.getLogger(Manager.class);

	private FrontendService frontendService;

	private boolean connect()
	{
		if (Configuration.get().getManagerAddress() == null)
			return false;

		if (frontendService == null)
		{
			try
			{
				RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();

				proxyFactory.setServiceInterface(FrontendService.class);
				proxyFactory.setServiceUrl("rmi://" + Configuration.get().getManagerAddress()
						+ "/frontendService");

				proxyFactory.afterPropertiesSet();

				frontendService = (FrontendService) proxyFactory.getObject();
			} catch (Exception e)
			{
				// Connection failed
				frontendService = null;
				return false;
			}
		}

		return true;
	}

	public ControllerInfo chooseAppServer(String appId)
	{
		if (!connect())
			return null;

		try
		{
			return frontendService.chooseAppServer(appId);
		} catch (Exception e)
		{
			logger.error("Connection to manager lost");
			frontendService = null;
		}

		return null;
	}

	public void newAppOrVersion(String appId)
	{
		if (!connect())
			return;

		try
		{
			frontendService.newAppOrVersion(appId);
		} catch (Exception e)
		{
			logger.error("Connection to manager lost");
			frontendService = null;
		}
	}

	public void setFrontendService(FrontendService frontendService)
	{
		this.frontendService = frontendService;
	}
}
