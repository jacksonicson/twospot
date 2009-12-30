package org.prot.storage;

import org.prot.stor.hbase.Key;
import org.prot.storage.query.StorageQuery;

public interface Storage
{
	public Key createObject(String appId, String kind, Key key, Object obj);

	public void updateObject(Key key, Object obj);

	public void deleteObject(Key key);

	public void query(StorageQuery query);
}
