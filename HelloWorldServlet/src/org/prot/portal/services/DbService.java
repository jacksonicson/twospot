package org.prot.portal.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.prot.portal.login.data.DataTablet;
import org.prot.portal.login.data.DbDao;

public class DbService
{
	private static final Logger logger = Logger.getLogger(DbService.class);
	
	private DbDao dbDao; 
	
	public List<String> getTables(String appId)
	{
		UserService userService = UserServiceFactory.getUserService(); 
		String username = userService.getCurrentUser();
		
		assert(username != null);
		
		return dbDao.getTables(username, appId);
	}

	public DataTablet getData(String tableName)
	{
		return dbDao.getTableData(tableName);
	}
	
	public void setDbDao(DbDao dbDao)
	{
		this.dbDao = dbDao;
	}
}
