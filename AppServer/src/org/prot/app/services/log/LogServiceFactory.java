package org.prot.app.services.log;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public class LogServiceFactory {
	private static final Logger logger = Logger.getLogger(LogServiceFactory.class);

	private static final String CONTROLLER_ADDRESS = "localhost";

	private static LogService logService;

	private static final int getRmiPort() {
		return Configuration.getInstance().getRmiRegistryPort();
	}

	private static LogService createLogService() {
		return new LogServiceImpl();

		// LogService logService = AccessController.doPrivileged(new
		// PrivilegedAction<LogService>()
		// {
		// public LogService run()
		// {
		// RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
		// proxyFactory.setServiceInterface(org.prot.controller.services.log.LogService.class);
		// proxyFactory.setServiceUrl("rmi://" + CONTROLLER_ADDRESS + ":" +
		// getRmiPort()
		// + "/appserver/LogService");
		// proxyFactory.afterPropertiesSet();
		//
		// Object object = proxyFactory.getObject();
		// if (object == null)
		// {
		// logger.error("Could not connect with the DbServices");
		// throw new NullPointerException();
		// }
		//
		// LogService service = new
		// LogServiceImpl((org.prot.controller.services.log.LogService) object);
		//
		// // Object o = userService;
		// return service;
		// }
		// });
		//
		// return logService;
	}

	private static LogService createMockLogService() {
		return new LogServiceMock();
	}

	public static LogService getLogService() {
		if (logService == null) {
			switch (Configuration.getInstance().getServerMode()) {
			case DEVELOPMENT:
				logService = createMockLogService();
				break;
			case SERVER:
				logService = createLogService();
				break;
			}
		}

		return logService;
	}
}
