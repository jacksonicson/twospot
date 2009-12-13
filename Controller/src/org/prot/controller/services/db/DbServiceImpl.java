package org.prot.controller.services.db;

import java.util.List;

import org.prot.controller.manager.TokenChecker;

public class DbServiceImpl implements DbService
{
	private TokenChecker tokenChecker;

	private DbDao dbDao;

	@Override
	public DataTablet getTableData(String token, String tableName, String startKey, long count)
	{
		if (tokenChecker.checkToken(token) == false)
			return null;

		return dbDao.getTableData(tableName, startKey, count);
	}

	@Override
	public List<String> getTables(String token, String appId)
	{
		if (tokenChecker.checkToken(token) == false)
			return null;

		return dbDao.getTables(appId);
	}

	public void setDbDao(DbDao dbDao)
	{
		this.dbDao = dbDao;
	}

	public void setTokenChecker(TokenChecker tokenChecker)
	{
		this.tokenChecker = tokenChecker;
	}
}
