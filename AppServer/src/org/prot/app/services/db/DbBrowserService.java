package org.prot.app.services.db;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.app.services.log.LogDao;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;

public class DbBrowserService
{
	private static final Logger logger = Logger.getLogger(DbBrowserService.class);

	private DbDao dbDao;
	
	private LogDao logDao;

	private UserService userService;

	DbBrowserService(DbDao dbDao, LogDao logDao)
	{
		this.dbDao = dbDao;
		this.logDao = logDao; 
	}

	private UserService getUserService()
	{
		if (this.userService == null)
			this.userService = UserServiceFactory.getUserService();

		return this.userService;
	}

	public List<String> getLogs(String appId)
	{
		return logDao.getLog(appId);
	}
	
	public List<String> getTables(String appId)
	{
		String user = getUserService().getCurrentUser();
		if (user == null)
		{
			logger.debug("Missing user info");
			return null;
		}

		return dbDao.getTables(user, appId);
	}

	public DataTablet getTableData(String tableName, String startKey, long count)
	{
		String user = getUserService().getCurrentUser();
		if (user == null)
		{
			logger.debug("Missing user info"); 
			return null;
		}

		return dbDao.getTableData(tableName);
	}
}
