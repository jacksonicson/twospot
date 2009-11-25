package org.prot.portal.login.data;

import java.util.List;

public interface DbDao
{
	public List<String> getTables(String username, String appId);
	
	public DataTablet getTableData(String tableName);
}
