package org.prot.frontend.cache.timeout;

import java.util.HashSet;
import java.util.Iterator;
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

	ControllerInfo pickController()
	{
		CachedControllerInfo[] infos = null;
		synchronized (this)
		{
			if (controllers.isEmpty())
				return null;

			infos = controllers.values().toArray(new CachedControllerInfo[0]);
		}

		// int select = Math.abs(counter++) % infos.length;
		long min = Long.MAX_VALUE;
		int select = -1;
		for (int i = 0; i < infos.length; i++)
		{
			if (min > infos[i].queue())
			{
				min = infos[i].queue();
				select = i;
			}
		}

		infos[select].increment();
		return infos[select];
	}

	synchronized void release(String address)
	{
		CachedControllerInfo controller = controllers.get(address);
		if (controller != null)
			controller.decrement();
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
