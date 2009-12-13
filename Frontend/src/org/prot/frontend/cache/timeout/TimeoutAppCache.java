package org.prot.frontend.cache.timeout;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.prot.frontend.cache.AppCache;
import org.prot.manager.data.ControllerInfo;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class TimeoutAppCache implements AppCache
{
	private static final Logger logger = Logger.getLogger(TimeoutAppCache.class);

	private static final int CONTROLLER_LIFETIME = 5 * 1000;

	private Map<String, CacheEntry> cache = new HashMap<String, CacheEntry>();

	public TimeoutAppCache()
	{
		Scheduler.addTask(new Updater());
	}

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
		if (entry == null)
			return null;

		return entry.pickController();
	}

	public void updateCache()
	{
		for (CacheEntry entry : cache.values())
		{
			// Remove all Controller entries which are older than the given
			// treshold
			entry.removeOlderThan(CONTROLLER_LIFETIME);
		}
	}

	class Updater extends SchedulerTask
	{
		@Override
		public long getInterval()
		{
			return 5000;
		}

		@Override
		public void run()
		{
			updateCache();
		}
	}
}
