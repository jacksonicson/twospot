package org.prot.manager.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.prot.util.managment.gen.ManagementData;

public class ControllerRegistry
{
	private static final Logger logger = Logger.getLogger(ControllerRegistry.class);

	private Map<String, ControllerInfo> controllerInfos = new ConcurrentHashMap<String, ControllerInfo>();
	
	private Map<String, ControllerStats> publicControllers = new HashMap<String, ControllerStats>();

	private Map<String, ControllerStats> controllers = new ConcurrentHashMap<String, ControllerStats>();

	public synchronized Map<String, ControllerInfo> getControllerInfos()
	{
		Map<String, ControllerInfo> copy = new HashMap<String, ControllerInfo>();
		copy.putAll(controllerInfos);
		return copy;
	}

	public synchronized ControllerInfo getControllerInfo(String address)
	{
		return controllerInfos.get(address);
	}
	
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
				ControllerInfo toUpdate = controllerInfos.get(address);
				toUpdate.update(info);
			} else
			{
				logger.info("New Controller: " + address);
				controllerInfos.put(address, info);
			}
		}

		// Remove all controllers which are not in the availableAddress list
		for (Iterator<String> itController = controllers.keySet().iterator(); itController.hasNext();)
		{
			String testAddress = itController.next();
			if (availableAddresses.contains(testAddress))
				continue;

			logger.debug("Removing Controller: " + testAddress);
			itController.remove();
		}
	}
	
	public synchronized void startUpdate()
	{
		// Do nothing
	}

	public synchronized void finalizeUpdate()
	{
		// Remove all old controllers
		removeOld();

		synchronized (publicControllers)
		{
			// Update public controllers
			publicControllers.putAll(controllers);

			// Remove all public controllers which are not in the controllers
			// map
			for (Iterator<String> it = publicControllers.keySet().iterator(); it.hasNext();)
			{
				String address = it.next();
				if (!controllers.containsKey(address))
					it.remove();
			}
		}
	}

	public synchronized void assignToController(String appId, String address)
	{
		ControllerStats stats = controllers.get(address);
		if (stats == null)
			return;

		stats.assign(appId);
	}

	public synchronized void removeController(String address)
	{
		controllers.remove(address);
	}

	public synchronized void updateController(ManagementData.Controller managementController)
	{
		String address = managementController.getAddress();
		ControllerStats controller = controllers.get(address);
		if (controller == null)
		{
			synchronized (controllers)
			{
				if (!controllers.containsKey(address))
				{
					controller = new ControllerStats(address);
					controllers.put(address, controller);
				}
			}
		}

		controller.updateStats(managementController);
	}

	private void removeOld()
	{
		for (Iterator<String> it = controllers.keySet().iterator(); it.hasNext();)
		{
			String address = it.next();
			ControllerStats controller = controllers.get(address);
			if (controller.isOld())
				it.remove();
		}
	}

	public boolean isEmpty()
	{
		synchronized (publicControllers)
		{
			return publicControllers.isEmpty();
		}
	}

	public Map<String, ControllerStats> getControllers()
	{
		synchronized (publicControllers)
		{
			Map<String, ControllerStats> copy = new HashMap<String, ControllerStats>();
			copy.putAll(publicControllers);
			return copy;
		}
	}

	public void dump()
	{
		for (ControllerStats controller : controllers.values())
			controller.dump();
	}
}
