package org.prot.manager.stats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.prot.util.managment.Ping;
import org.prot.util.stats.StatsValue;

public class Stats
{
	private Map<String, ControllerStats> publicControllers = new ConcurrentHashMap<String, ControllerStats>();

	private HashMap<String, ControllerStats> controllers = new HashMap<String, ControllerStats>();

	public void startUpdate()
	{
		// Do nothing
	}

	public void finalizeUpdate()
	{
		removeOld();

		publicControllers.putAll(controllers);

		for (Iterator<String> it = publicControllers.keySet().iterator(); it.hasNext();)
		{
			String address = it.next();
			if (!controllers.containsKey(address))
				it.remove();
		}
	}

	public void remove(String address)
	{
		controllers.remove(address);
	}

	public void update(String address, Ping ping)
	{
		// Load management data
		Set<StatsValue> stats = ping.ping();

		// Update local controller data
		ControllerStats controller = controllers.get(address);
		if (controller == null)
		{
			controller = new ControllerStats(address);
			controllers.put(address, controller);
		}
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
