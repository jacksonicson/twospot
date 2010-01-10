package org.prot.manager.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class ControllerRegistry
{
	private static final Logger logger = Logger.getLogger(ControllerRegistry.class);

	private Map<String, ControllerInfo> controllers = new ConcurrentHashMap<String, ControllerInfo>();

	public synchronized void update(List<ControllerInfo> infos)
	{
		// Contains alls addresses from the controllers
		Set<String> availableAddresses = new HashSet<String>();

		// Update existing and add new controllers
		for (ControllerInfo info : infos)
		{
			String address = info.getAddress();
			availableAddresses.add(address);

			if (controllers.containsKey(address))
			{
				logger.info("Updating Controller: " + address);
				ControllerInfo toUpdate = controllers.get(address);
				toUpdate.update(info);
			} else
			{
				logger.info("New Controller: " + address);
				controllers.put(address, info);
			}
		}

		// Remove all controllers which are not in the availableAddress list
		for (Iterator<String> itController = controllers.keySet().iterator(); itController.hasNext();)
		{
			String testAddress = itController.next();
			if (availableAddresses.contains(testAddress))
				continue;

			itController.remove();
		}
	}

	public synchronized Map<String, ControllerInfo> getControllers()
	{
		Map<String, ControllerInfo> copy = new HashMap<String, ControllerInfo>();
		copy.putAll(controllers);
		return copy;
	}

	public synchronized ControllerInfo getController(String address)
	{
		return controllers.get(address);
	}
}
