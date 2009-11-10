package org.prot.frontend.cache;

import java.util.HashMap;
import java.util.Map;

import org.prot.manager.config.ControllerInfo;

public class TimeoutAppCache implements AppCache
{
	private Map<String, CacheEntry> cache = new HashMap<String, CacheEntry>();

	@Override
	public void cacheController(String appId, ControllerInfo controller)
	{
		CacheEntry entry = new CacheEntry();
		entry.setTimestamp(System.currentTimeMillis());
		entry.addController(controller);
		entry.setAppId(appId);

		cache.put(appId, entry);
	}

	@Override
	public ControllerInfo getController(String appId)
	{
		CacheEntry entry = cache.get(appId);
		if(entry == null)
			return null; 
		
		return entry.pickController(); 
	}

	@Override
	public void updateCache()
	{
		// TODO: Delete old cache entries
	}
}
