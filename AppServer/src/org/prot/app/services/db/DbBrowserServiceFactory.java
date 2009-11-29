package org.prot.app.services.db;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.prot.app.services.log.HBaseLogDao;
import org.prot.app.services.log.LogDao;

public class DbBrowserServiceFactory
{
	private static final Logger logger = Logger.getLogger(DbBrowserServiceFactory.class);

	private static DbBrowserService service;

	public static final DbBrowserService getDbBrowserService()
	{
		if (service == null)
		{
			try
			{
				DbDao dbDao = new HbaseDbDao();
				LogDao logDao = new HBaseLogDao();
				service = new DbBrowserService(dbDao, logDao);
			} catch (IOException e)
			{
				logger.error("Could not create DbBrowserService", e);
			}
		}

		return service;
	}
}
