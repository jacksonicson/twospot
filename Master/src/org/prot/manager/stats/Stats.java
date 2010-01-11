package org.prot.manager.stats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class Stats
{
	private static final Logger logger = Logger.getLogger(Stats.class);

	private Map<String, ControllerStats> publicControllers = new HashMap<String, ControllerStats>();

	private Map<String, ControllerStats> controllers = new ConcurrentHashMap<String, ControllerStats>();

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

	public synchronized void updateController(String address)
	{
		// // Load management data
		// Set<StatsValue> stats = ping.ping();
		//
		// ControllerStats controller = controllers.get(address);
		//
		// // Need to create a new ControllerStats-Object?
		// if (controller == null)
		// {
		// synchronized (controllers)
		// {
		// // Double check this
		// if (!controllers.containsKey(address))
		// {
		// // Create and register a new ControllerStats-Object
		// controller = new ControllerStats(address);
		// controllers.put(address, controller);
		// }
		//
		// }
		// }
		//
		// // Update the ControllerStas object
		// controller.updateStats(stats);
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
