package org.prot.controller.services.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

class HbaseDbDao implements DbDao
{
	private static final Logger logger = Logger.getLogger(HbaseDbDao.class);

	@Override
	public void getTableData(final String tableName, final String startKey, final long count)
	{
		return;
	}

	@Override
	public List<String> getTables(final String appId)
	{
		return new ArrayList<String>();
	}

}
