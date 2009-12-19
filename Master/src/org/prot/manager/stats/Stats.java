package org.prot.manager.stats;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.prot.util.managment.Ping;
import org.prot.util.stats.StatsValue;

public class Stats
{
	private Map<String, ControllerStats> publicControllers = new ConcurrentHashMap<String, ControllerStats>();

	private Map<String, ControllerStats> controllers = new ConcurrentHashMap<String, ControllerStats>();

	public void startUpdate()
	{
		// Do nothing
	}

	public void finalizeUpdate()
	{
		// Remove all old controllers
		removeOld();

		// Update public controllers
		publicControllers.putAll(controllers);

		// Remove all public controllers which are not in the controllers map
		for (Iterator<String> it = publicControllers.keySet().iterator(); it.hasNext();)
		{
			String address = it.next();
			if (!controllers.containsKey(address))
				it.remove();
		}
	}

	public void assignToController(String appId, String address)
	{
		ControllerStats stats = controllers.get(address);
		if (stats == null)
			return;

		stats.assign(appId);
	}

	public void removeController(String address)
	{
		synchronized (controllers)
		{
			// Remove controller
			controllers.remove(address);
		}
	}

	public void updateController(String address, Ping ping)
	{
		// Load management data
		Set<StatsValue> stats = ping.ping();

		ControllerStats controller = controllers.get(address);

		// Need to create a new ControllerStats-Object?
		if (controller == null)
		{
			synchronized (controllers)
			{
				// Double check this
				if (controllers.containsKey(address))
				{
					// Create and register a new ControllerStats-Object
					controller = new ControllerStats(address);
					controllers.put(address, controller);
				}

			}
		}

		// Update the ControllerStas object
		controller.updateStats(stats);
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

	public Map<String, ControllerStats> getControllers()
	{
		return publicControllers;
	}

	public void dump()
	{
		for (ControllerStats controller : controllers.values())
			controller.dump();
	}
}
