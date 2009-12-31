package org.prot.app.services.db;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.db.DbService;

public final class DbBrowserService
{
	private static final Logger logger = Logger.getLogger(DbBrowserService.class);

	private final DbService dbService;

	private UserService userService;

	DbBrowserService(DbService dbService)
	{
		this.dbService = dbService;
	}

	private UserService getUserService()
	{
		if (this.userService == null)
			this.userService = UserServiceFactory.getUserService();

		return this.userService;
	}

	public List<String> getTables(String appId)
	{
		String user = getUserService().getCurrentUser();
		if (user == null)
		{
			logger.debug("User must be logged in to browse tables");
			return null;
		}

		return dbService.getTables(Configuration.getInstance().getAuthenticationToken(), appId);
	}

	public Object getTableData(String tableName, String startKey, long count)
	{
		String user = getUserService().getCurrentUser();
		if (user == null)
		{
			logger.debug("User msut be logged in to browse tables");
			return null;
		}

		return null;
	}
}
