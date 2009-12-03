package org.prot.app.services.db;

import java.util.ArrayList;
import java.util.List;

public class MockDbDao implements DbDao
{
	@Override
	public DataTablet getTableData(String tableName)
	{
		// Not supported in the mock
		return new DataTablet();
	}

	@Override
	public List<String> getTables(String username, String appId)
	{
		// Not supported in the mock
		return new ArrayList<String>();
	}

}
