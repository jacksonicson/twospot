package org.prot.controller.services.db;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.Storage;
import org.prot.storage.StorageImpl;

public class HbaseDbDao implements DbDao {
	private static final Logger logger = Logger.getLogger(HbaseDbDao.class);

	@Override
	public void getTableData(final String tableName, final String startKey, final long count) {
		return;
	}

	@Override
	public List<String> getTables(final String appId) {
		Storage storage = new StorageImpl();
		return storage.listKinds(appId);
	}

}
