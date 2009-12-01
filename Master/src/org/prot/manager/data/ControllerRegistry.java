package org.prot.manager.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class ControllerRegistry
{
	private static final Logger logger = Logger.getLogger(ControllerRegistry.class);

	private Map<String, ControllerInfo> controllers = new HashMap<String, ControllerInfo>();

	public synchronized void update(List<ControllerInfo> infos)
	{
		logger.debug("Updating controller registry");

		// Contains alls addresses from the controllers
		Set<String> availableAddresses = new HashSet<String>();

		// Update existing and add new controllers
		for (ControllerInfo info : infos)
		{
			String address = info.getAddress();
			availableAddresses.add(address);

			if (controllers.containsKey(address))
			{
				ControllerInfo toUpdate = controllers.get(address);
				toUpdate.update(info);
			} else
			{
				controllers.put(address, info);
			}
		}

		// Find all controllers to remove
		Set<String> toRemove = new HashSet<String>();
		for (String address : controllers.keySet())
		{
			if (availableAddresses.contains(address) == false)
				toRemove.add(address);
		}

		// Finally remove the controllers
		for (String remove : toRemove)
		{
			logger.info("Removing controller: " + remove);
			controllers.remove(remove);
		}
	}

	public synchronized void fetchControllerData()
	{
		// TODO!
	}

	public Collection<ControllerInfo> getControllers()
	{
		return controllers.values();
	}
}
