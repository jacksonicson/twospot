package org.prot.storage;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.dev.MemStorage;
import org.prot.storage.query.StorageQuery;

public class StorageDev implements Storage
{
	private static final Logger logger = Logger.getLogger(StorageDev.class);

	private MemStorage memStorage;

	public StorageDev()
	{
		this.memStorage = new MemStorage();
	}

	@Override
	public List<Key> createKey(String appId, long amount)
	{
		return memStorage.createKey(amount);
	}

	@Override
	public void createObject(String appId, String kind, Key key, byte[] obj)
	{
		memStorage.addObj(kind, key, obj);
	}

	@Override
	public boolean deleteObject(String appId, String kind, Key key)
	{
		return memStorage.removeObj(kind, key);
	}

	@Override
	public void updateObject(String appId, String kind, Key key, byte[] obj)
	{
		memStorage.updateObject(kind, key, obj);
	}

	@Override
	public List<byte[]> query(StorageQuery query)
	{
		return null;
	}
}
