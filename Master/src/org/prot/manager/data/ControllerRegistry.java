package org.prot.manager.data;

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
				logger.info("Updating Controller: " + address);

				ControllerInfo toUpdate = controllers.get(address);
				toUpdate.update(info);
			} else
			{
				logger.info("New Controller: " + address);
				controllers.put(address, info);
			}
		}

		// Search controllers to delete
		Set<String> allControllers = new HashSet<String>();
		allControllers.addAll(controllers.keySet());
		for (String address : availableAddresses)
			allControllers.remove(address);

		// Delete all remaining controllers
		for (String delete : allControllers)
			controllers.remove(delete);
	}

	public synchronized Set<ControllerInfo> getControllers()
	{
		Set<ControllerInfo> controllers = new HashSet<ControllerInfo>();
		controllers.addAll(this.controllers.values());
		return controllers;
	}
}
