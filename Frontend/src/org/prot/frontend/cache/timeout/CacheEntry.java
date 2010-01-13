package org.prot.frontend.cache.timeout;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.prot.manager.stats.ControllerInfo;

public class CacheEntry
{
	private static final Logger logger = Logger.getLogger(CacheEntry.class);

	private final String appId;

	private Map<String, CachedControllerInfo> controllers = new ConcurrentHashMap<String, CachedControllerInfo>();

	private Map<String, Long> requestCounter = new ConcurrentHashMap<String, Long>();

	public CacheEntry(String appId)
	{
		this.appId = appId;
	}

	synchronized boolean hasControllers()
	{
		return controllers.size() > 0;
	}

	synchronized void updateControllers(Set<ControllerInfo> infos)
	{
		Set<String> addresses = new HashSet<String>();

		// Add all new Controllers
		for (ControllerInfo info : infos)
		{
			String address = info.getAddress();
			addresses.add(address);

			if (!controllers.containsKey(address))
			{
				CachedControllerInfo newEntry = new CachedControllerInfo(info);
				newEntry.setTimestamp(System.currentTimeMillis());
				controllers.put(address, newEntry);
			}
		}

		// Remove all old Controllers
		for (Iterator<String> keyIt = controllers.keySet().iterator(); keyIt.hasNext();)
		{
			String test = keyIt.next();
			if (!addresses.contains(test))
				keyIt.remove();
		}

		logger.debug("Known controllers: " + controllers.keySet().size());
	}

	synchronized void removeStale(String address)
	{
		controllers.remove(address);
	}

	synchronized void removeOlderThan(long threshold)
	{
		long currentTime = System.currentTimeMillis();
		for (Iterator<String> keyIt = controllers.keySet().iterator(); keyIt.hasNext();)
		{
			String address = keyIt.next();
			CachedControllerInfo info = controllers.get(address);
			if ((currentTime - info.getTimestamp()) > threshold)
				keyIt.remove();
		}
	}

	synchronized void block(String address)
	{
		CachedControllerInfo info = controllers.get(address);
		if (info != null)
		{
			logger.debug("Blocking Controller " + address);
			info.setBlocked(true);
		}
	}

	ControllerInfo pickController()
	{
		LinkedList<CachedControllerInfo> controllers = new LinkedList<CachedControllerInfo>();
		synchronized (this)
		{
			if (this.controllers.isEmpty())
				return null;

			controllers.addAll(this.controllers.values());
		}

		long min = Long.MAX_VALUE;
		CachedControllerInfo selected = null;

		for (CachedControllerInfo info : controllers)
		{
			if (info.isBlocked())
				continue;

			Long count = requestCounter.get(info.getAddress());
			if (count == null)
			{
				synchronized (requestCounter)
				{
					count = requestCounter.get(info.getAddress());
					if (count == null)
					{
						requestCounter.put(info.getAddress(), 0l);
					}
				}

				selected = info;
				break;
			} else
			{
				if (min > count)
				{
					min = count;
					selected = info;
				}
			}
		}

		if (selected != null)
			aquire(selected.getAddress());

		return selected;
	}

	void aquire(String address)
	{
		synchronized (requestCounter)
		{
			Long min = requestCounter.get(address);
			min++;
			requestCounter.put(address, min);
		}
	}

	void release(String address)
	{
		synchronized (requestCounter)
		{
			Long counter = requestCounter.get(address);
			if (counter != null)
			{
				if (counter > 0)
				{
					counter--;
					requestCounter.put(address, counter);
				}
			}
		}
	}

	String getAppId()
	{
		return appId;
	}

	public int hashCode()
	{
		return appId.hashCode();
	}

	public String toString()
	{
		return appId;
	}
}
