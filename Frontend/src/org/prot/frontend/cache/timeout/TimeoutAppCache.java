package org.prot.frontend.cache.timeout;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.prot.frontend.ExceptionSafeFrontendProxy;
import org.prot.frontend.cache.AppCache;
import org.prot.manager.services.FrontendService;
import org.prot.manager.stats.ControllerInfo;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class TimeoutAppCache implements AppCache
{
	private static final Logger logger = Logger.getLogger(TimeoutAppCache.class);

	private static final int CONTROLLER_LIFETIME = 5 * 1000;

	private Map<String, CacheEntry> cache = new ConcurrentHashMap<String, CacheEntry>();

	private FrontendService frontendService;

	public TimeoutAppCache()
	{
		Scheduler.addTask(new Updater());

		frontendService = (FrontendService) ExceptionSafeFrontendProxy.newInstance(getClass()
				.getClassLoader(), FrontendService.class);
	}

	private void updateControllers(String appId)
	{
		CacheEntry entry = cache.get(appId);
		if (entry == null)
		{
			synchronized (cache)
			{
				if (!cache.containsKey(appId))
				{
					entry = new CacheEntry(appId);
					cache.put(appId, entry);
				}
			}
		}

		synchronized (entry)
		{
			if (entry.hasControllers())
				return;

			Set<ControllerInfo> infoset = frontendService.selectController(appId);
			if (infoset != null)
				entry.updateControllers(infoset);
		}
	}

	@Override
	public ControllerInfo getController(String appId)
	{
		CacheEntry entry = cache.get(appId);
		if (entry == null)
		{
			updateControllers(appId);
			entry = cache.get(appId);
		}

		ControllerInfo info = entry.pickController();
		if (info == null)
		{
			updateControllers(appId);
			entry = cache.get(appId);
			info = entry.pickController();
		}

		if (info == null)
			logger.warn("Could not find a Controller for: " + appId);

		return info;
	}

	@Override
	public void staleController(String address)
	{
		for (CacheEntry entry : cache.values())
			entry.removeStale(address);
	}

	public void updateCache()
	{
		for (CacheEntry entry : cache.values())
			entry.removeOlderThan(CONTROLLER_LIFETIME);
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
