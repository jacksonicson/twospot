package org.prot.app.services.db;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.prot.app.services.log.HBaseLogDao;
import org.prot.app.services.log.LogDao;
import org.prot.app.services.log.MockLogDao;
import org.prot.appserver.config.Configuration;

public class DbBrowserServiceFactory
{
	private static final Logger logger = Logger.getLogger(DbBrowserServiceFactory.class);

	private static DbBrowserService service;

	private static DbBrowserService createDbBrowserService()
	{
		try
		{
			DbDao dbDao = new HbaseDbDao();
			LogDao logDao = new HBaseLogDao();
			DbBrowserService service = new DbBrowserService(dbDao, logDao);
			return service;

		} catch (IOException e)
		{
			logger.error("Could not create DbBrowserService", e);
		}

		return null;
	}

	private static DbBrowserService createMockDbBrowserService()
	{
		DbDao dbDao = new MockDbDao();
		LogDao logDao = new MockLogDao();
		return new DbBrowserService(dbDao, logDao);
	}

	public static final DbBrowserService getDbBrowserService()
	{
		if (service == null)
		{
			switch (Configuration.getInstance().getServerMode())
			{
			case DEVELOPMENT:
				service = createDbBrowserService();
				break;
			case SERVER:
				service = createMockDbBrowserService();
				break;
			}
		}

		return service;
	}
}
