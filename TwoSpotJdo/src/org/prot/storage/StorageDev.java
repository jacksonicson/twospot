package org.prot.storage;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.util.Bytes;
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

	private long counter = 0;

	@Override
	public List<Key> createKey(String appId, long amount)
	{
		counter++;

		List<Key> keyList = new ArrayList<Key>();
		while (amount-- > 0)
		{
			byte[] bTime = Bytes.toBytes(System.currentTimeMillis());
			byte[] bCounter = Bytes.toBytes(counter);
			byte[] bKey = Bytes.add(bTime, bCounter);
			Key key = new Key();
			key.setKey(bKey);

			keyList.add(key);
		}

		return keyList;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] query(String appId, String kind, Key key)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
