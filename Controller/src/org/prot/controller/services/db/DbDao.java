package org.prot.controller.services.db;

import java.util.List;

interface DbDao
{
	public List<String> getTables(String appId);
	
	public DataTablet getTableData(String tableName);
}
