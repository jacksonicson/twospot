package org.prot.app.services.db;

import java.util.List;

interface DbDao
{
	public List<String> getTables(String username, String appId);
	
	public DataTablet getTableData(String tableName);
}
