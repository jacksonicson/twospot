package org.prot.manager.services;

import org.apache.log4j.Logger;
import org.prot.controller.services.ControllerService;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;
import org.prot.manager.exceptions.MissingControllerException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class FrontendServiceImpl implements FrontendService
{
	private static final Logger logger = Logger.getLogger(FrontendServiceImpl.class);

	private ControllerRegistry registry;

	@Override
	public ControllerInfo chooseAppServer(String appId) throws MissingControllerException
	{
		ControllerInfo info = registry.selectController();
		if (info == null)
			throw new MissingControllerException();

		return info;
	}

	@Override
	public void newAppOrVersion(String appId)
	{
		logger.info("new app or version: " + appId);
		for (ControllerInfo info : registry.getControllers())
		{
			logger.info("Informing controller: " + info.getServicePort());

			try
			{
				RmiProxyFactoryBean proxy = new RmiProxyFactoryBean();
				proxy.setServiceInterface(ControllerService.class);
				proxy.setServiceUrl(info.getServiceAddress() + ":" + info.getServicePort() + "/"
						+ info.getServiceName());
				proxy.afterPropertiesSet();

				ControllerService service = (ControllerService) proxy.getObject();
				service.updateApp(appId);

			} catch (Exception e)
			{
				logger.error("could not connect to the controller - controller is dead?" + e.getMessage());
			}
		}
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
