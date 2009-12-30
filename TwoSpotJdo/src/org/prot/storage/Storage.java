package org.prot.storage;

import java.util.List;
import java.util.Map;

import org.prot.stor.hbase.Key;
import org.prot.storage.query.StorageQuery;

public interface Storage
{
	public List<Key> createKey(String appId, String kind, int amount);

	public void createObject(String appId, String kind, Key key, Object obj, Map<String, byte[]> index);

	public void updateObject(Key key, Object obj);

	public void deleteObject(Key key);

	public void query(StorageQuery query);
}
