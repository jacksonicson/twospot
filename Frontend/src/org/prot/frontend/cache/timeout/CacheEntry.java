package org.prot.frontend.cache.timeout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.manager.data.ControllerInfo;

public class CacheEntry
{
	private static final Logger logger = Logger.getLogger(CacheEntry.class);

	private final String appId;

	private Map<String, CachedControllerInfo> controllers = new HashMap<String, CachedControllerInfo>();

	public CacheEntry(String appId)
	{
		this.appId = appId;
	}

	boolean hasControllers()
	{
		return controllers.size() > 0;
	}

	synchronized void updateControllers(Set<ControllerInfo> infos)
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

	synchronized void removeOlderThan(long threshold)
	{
		logger.debug("Removing old Controllers");

		long currentTime = System.currentTimeMillis();
		for (CachedControllerInfo controller : controllers.values().toArray(new CachedControllerInfo[0]))
		{
			if ((currentTime - controller.getTimestamp()) > threshold)
				controllers.remove(controller.getAddress());
		}
	}

	ControllerInfo pickController()
	{
		if (controllers.isEmpty())
			return null;

		// Cycles through all controllers to balance the requests
		String[] keys = controllers.keySet().toArray(new String[0]);
		return controllers.get(keys[0]);
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
