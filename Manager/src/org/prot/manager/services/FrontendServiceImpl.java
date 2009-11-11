package org.prot.manager.services;

import org.apache.log4j.Logger;
import org.prot.controller.services.ControllerService;
import org.prot.manager.config.ControllerInfo;
import org.prot.manager.config.StaticConfiguration;
import org.prot.manager.exceptions.MissingControllerException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class FrontendServiceImpl implements FrontendService
{
	private static final Logger logger = Logger.getLogger(FrontendServiceImpl.class);

	private StaticConfiguration configuration = new StaticConfiguration();

	@Override
	public ControllerInfo chooseAppServer(String appId) throws MissingControllerException
	{
		logger.info("frontend requests an appserver info");
		return (ControllerInfo) configuration.getControllers().toArray()[0];
	}

	@Override
	public void newAppOrVersion(String appId)
	{
		logger.info("new app or version: " + appId);

		// Inform all Controllers
		for (ControllerInfo info : configuration.getControllers())
		{
			logger.info("Informing controller: " + info.getServicePort());
			
			RmiProxyFactoryBean proxy = new RmiProxyFactoryBean(); 
			proxy.setServiceInterface(ControllerService.class); 
			proxy.setServiceUrl("rmi://localhost:" + info.getServicePort() + "/" + info.getServiceName());
			proxy.afterPropertiesSet(); 
			ControllerService service = (ControllerService)proxy.getObject();
			service.updateApp(appId); 
		}
	}
}
