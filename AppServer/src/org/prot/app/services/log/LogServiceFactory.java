package org.prot.app.services.log;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public class LogServiceFactory
{
	private static final Logger logger = Logger.getLogger(LogServiceFactory.class);

	private static LogService logService;

	private static LogService createLogService()
	{
		LogDao dao;
		LogService logService;
		try
		{
			dao = new HBaseLogDao();
			logService = new LogService(Configuration.getInstance().getAppId(), dao);
			return logService;

		} catch (IOException e)
		{
			logger.error("Could not create LogService", e);
		}

		return null;
	}

	private static LogService createMockLogService()
	{
		LogDao dao = new MockLogDao();
		return new LogService(Configuration.getInstance().getAppId(), dao);
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
