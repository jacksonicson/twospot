package org.prot.storage;

import java.util.List;

import org.prot.storage.query.StorageQuery;

public interface Storage
{
	public List<Key> createKey(String appId, long amount);

	public void createObject(String appId, String kind, Key key, byte[] obj);

	public void updateObject(String appId, String kind, Key key, byte[] obj);

	public boolean deleteObject(String appId, String kind, Key key);

	public List<byte[]> query(StorageQuery query);
}
