package org.prot.storage;

import java.util.List;

import org.prot.storage.query.StorageQuery;

public class StorageDev implements Storage
{

	@Override
	public List<Key> createKey(String appId, long amount)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createObject(String appId, String kind, Key key, byte[] obj)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteObject(String appId, String kind, Key key)
	{
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public void updateObject(String appId, String kind, Key key, byte[] obj)
	{
		// TODO Auto-generated method stub

	}
}
