package org.prot.storage;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.dev.DevQueryHandler;
import org.prot.storage.dev.MemStorage;
import org.prot.storage.query.QueryEngine;
import org.prot.storage.query.StorageQuery;

public class StorageDev implements Storage {
	private static final Logger logger = Logger.getLogger(StorageDev.class);

	private MemStorage memStorage;

	public StorageDev() {
		this.memStorage = new MemStorage();
	}

	@Override
	public List<Key> createKey(String appId, long amount) {
		return memStorage.createKey(amount);
	}

	@Override
	public void createObject(String appId, String kind, Key key, byte[] obj) {
		memStorage.addObj(kind, key, obj);
	}

	@Override
	public boolean deleteObject(String appId, String kind, Key key) {
		return memStorage.removeObj(kind, key);
	}

	@Override
	public void updateObject(String appId, String kind, Key key, byte[] obj) {
		memStorage.updateObject(kind, key, obj);
	}

	@Override
	public List<byte[]> query(StorageQuery query) {
		DevQueryHandler handler = new DevQueryHandler(memStorage);
		QueryEngine engine = new QueryEngine(handler);
		return engine.run(query);
	}

	@Override
	public byte[] query(String appId, Key key) {
		DevQueryHandler handler = new DevQueryHandler(memStorage);
		QueryEngine engine = new QueryEngine(handler);
		return engine.fetch(appId, key);
	}

	@Override
	public List<String> listKinds(String appId) {
		throw new NotImplementedException();
	}

	@Override
	public List<byte[]> scanEntities(String appId, String kind) {
		throw new NotImplementedException();
	}
}
