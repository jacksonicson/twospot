package org.prot.app.services.log;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public class LogServiceFactory
{
	private static final Logger logger = Logger.getLogger(LogServiceFactory.class);

	private static LogService logService;

	private static LogService createLogService()
	{
		return new LogService();
	}

	private static LogService createMockLogService()
	{
		return null;
	}

	public static LogService getLogService()
	{
		if (logService == null)
		{
			switch (Configuration.getInstance().getServerMode())
			{
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
