package org.prot.storage;

import java.util.List;
import java.util.Map;

import org.prot.storage.query.StorageQuery;

public interface Storage
{
	public List<Key> createKey(String appId, long amount);

	public void createObject(String appId, String kind, Key key, Object obj, Map<String, byte[]> index,
			IndexDefinition indexDef);

	public void updateObject(Key key, Object obj);

	public boolean deleteObject(String appId, String kind, Key key);

	public List<Object> query(StorageQuery query);
}
