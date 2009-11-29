package org.prot.app.services.log;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public class LogServiceFactory
{
	private static final Logger logger = Logger.getLogger(LogServiceFactory.class);

	private static LogService logService;

	public static LogService getLogService()
	{
		if (logService == null)
		{
			LogDao dao;
			try
			{
				dao = new HBaseLogDao();
				logService = new LogService(Configuration.getInstance().getAppId(), dao);
				
			} catch (IOException e)
			{
				logger.error("Could not create LogService", e);
			}
		}

		return logService;
	}
}
