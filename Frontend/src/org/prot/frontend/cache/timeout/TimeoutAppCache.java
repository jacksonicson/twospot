package org.prot.frontend.cache.timeout;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.prot.frontend.cache.AppCache;
import org.prot.manager.config.ControllerInfo;

public class TimeoutAppCache implements AppCache
{
	private static final Logger logger = Logger.getLogger(TimeoutAppCache.class);
	
	private static final int CONTROLLER_LIFETIME = 5 * 1000; 
	
	private Map<String, CacheEntry> cache = new HashMap<String, CacheEntry>();

	@Override
	public void cacheController(String appId, ControllerInfo controller)
	{
		CacheEntry entry = new CacheEntry();
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
		for(CacheEntry entry : cache.values())
		{
			// Remove all Controller entries which are older than the given treshold
			entry.removeOlderThan(CONTROLLER_LIFETIME); 
		}
	}
}
