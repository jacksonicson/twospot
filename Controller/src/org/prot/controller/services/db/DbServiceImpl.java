package org.prot.controller.services.db;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.controller.app.TokenChecker;
import org.prot.storage.Storage;
import org.prot.storage.StorageImpl;

public class DbServiceImpl implements DbService {
	private static final Logger logger = Logger.getLogger(DbServiceImpl.class);

	private TokenChecker tokenChecker;

	private DbDao dbDao;

	@Override
	public List<byte[]> getTableData(String token, String appId, String kind) {
		if (tokenChecker.checkToken(token) == false)
			return null;

		Storage storage = new StorageImpl();
		List<byte[]> data = storage.scanEntities(appId, kind);

		logger.info("Fetched: " + data.size());

		return data;
	}

	@Override
	public List<String> getTables(String token, String appId) {
		if (tokenChecker.checkToken(token) == false)
			return null;

		return dbDao.getTables(appId);
	}

	public void setDbDao(DbDao dbDao) {
		this.dbDao = dbDao;
	}

	public void setTokenChecker(TokenChecker tokenChecker) {
		this.tokenChecker = tokenChecker;
	}
}
