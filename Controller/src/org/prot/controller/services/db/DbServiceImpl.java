package org.prot.controller.services.db;

import java.util.List;

import org.prot.controller.manager.AppManager;

public class DbServiceImpl implements DbService
{
	private AppManager appManager;

	private DbDao dbDao;

	@Override
	public DataTablet getTableData(String token, String tableName, String startKey, long count)
	{
		if (appManager.checkToken(token) == false)
			return null;

		return dbDao.getTableData(tableName, startKey, count);
	}

	@Override
	public List<String> getTables(String token, String appId)
	{
		if (appManager.checkToken(token) == false)
			return null;

		return dbDao.getTables(appId);
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	public void setDbDao(DbDao dbDao)
	{
		this.dbDao = dbDao;
	}
}
