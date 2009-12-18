package org.prot.frontend.cache.timeout;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
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

	boolean hasControllers()
	{
		return controllers.size() > 0;
	}

	void updateControllers(Set<ControllerInfo> infos)
	{
		logger.debug("Updating controllers");

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
		for (String test : controllers.keySet().toArray(new String[0]))
		{
			if (!addresses.contains(test))
			{
				logger.debug("Removing controller");
				controllers.remove(test);
			}
		}

		logger.debug("SIZE: " + controllers.keySet().size());
	}

	void removeOlderThan(long threshold)
	{
		long currentTime = System.currentTimeMillis();
		for (CachedControllerInfo controller : controllers.values().toArray(new CachedControllerInfo[0]))
		{
			if ((currentTime - controller.getTimestamp()) > threshold)
				controllers.remove(controller.getAddress());
		}
	}

	Random r = new Random();
	int counter = 0;

	ControllerInfo pickController()
	{
		if (controllers.isEmpty())
			return null;

		ControllerInfo[] infos = controllers.values().toArray(new ControllerInfo[0]);
		int select = Math.abs(counter++) % infos.length;

		return infos[select];
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
