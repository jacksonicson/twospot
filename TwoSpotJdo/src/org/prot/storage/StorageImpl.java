package org.prot.storage;

import org.prot.stor.hbase.Key;
import org.prot.storage.query.StorageQuery;

public class StorageImpl implements Storage
{
	public StorageImpl()
	{

	}

	@Override
	public Key createObject(String appId, String kind, Key key, Object obj)
	{
		return null;
	}

	@Override
	public void deleteObject(Key key)
	{

	}

	@Override
	public void query(StorageQuery query)
	{

	}

	@Override
	public void updateObject(Key key, Object obj)
	{

	}
}
