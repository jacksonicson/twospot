package org.prot.manager.stats;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.prot.util.managment.gen.ManagementData;
import org.prot.util.managment.gen.ManagementData.AppServer;

public class ControllerStats
{
	private static final Logger logger = Logger.getLogger(ControllerStats.class);

	// Identifies the controller
	private final String address;

	// Last update
	private long lastUpdate;

	// Applications which are currently managed by the Controller
	private Set<String> runningApps = new HashSet<String>();

	// Stores all isntance data for each application
	private Map<String, InstanceStats> instances = new ConcurrentHashMap<String, InstanceStats>();

	public class StatValues
	{
		public double cpu;
		public double rps;

		public long freeMemory;
		public long totalMemory;

		public boolean overloaded;

		public void dump()
		{
			logger.debug("   CPU: " + cpu);
			logger.debug("   RPS: " + rps);
			logger.debug("   FreeMem: " + freeMemory);
			logger.debug("   TotalMem: " + totalMemory);
		}
	}

	private final StatValues stats = new StatValues();

	ControllerStats(String address)
	{
		this.address = address;
	}

	public int size()
	{
		return instances.size();
	}

	public InstanceStats getInstance(String appId)
	{
		return instances.get(appId);
	}

	synchronized boolean isOld()
	{
		return System.currentTimeMillis() - lastUpdate > 60 * 1000;
	}

	synchronized void assign(String appId)
	{
		if (instances.containsKey(appId))
			return;

		runningApps.add(appId);
		instances.put(appId, new InstanceStats(appId, true));
	}

	synchronized void updateStats(ManagementData.Controller controller)
	{
		// Update the timestamp
		lastUpdate = System.currentTimeMillis();

		// Update Controller stats
		this.stats.cpu = controller.getCpu();
		this.stats.freeMemory = controller.getFreeMem();
		this.stats.overloaded = controller.getOverloaded();
		this.stats.rps = controller.getRps();
		this.stats.totalMemory = controller.getTotalMem();

		Set<String> tmpApps = new HashSet<String>();
		for (AppServer appServer : controller.getAppServersList())
		{
			String appId = appServer.getAppId();
			tmpApps.add(appId);
			if (!runningApps.contains(appId))
			{
				runningApps.add(appId);
				instances.put(appId, new InstanceStats(appId));
			}

			// Update instace stats
			InstanceStats instanceStats = instances.get(appId);
			instanceStats.update(appServer);
		}

		// Remove all old applications
		for (Iterator<String> it = runningApps.iterator(); it.hasNext();)
		{
			String runningAppId = it.next();
			if (!tmpApps.contains(runningAppId))
			{
				if (instances.get(runningAppId).decrementAssignmentCounter() == false)
				{
					logger.debug("Removing from Controller: " + runningAppId);
					instances.remove(runningAppId);
					it.remove();
				}
			}
		}
	}

	public StatValues getValues()
	{
		return stats;
	}

	public String getAddress()
	{
		return address;
	}

	public void dump()
	{
		logger.debug("=============================");
		logger.debug("Controller: " + this.address);
		stats.dump();
		logger.debug(" ");
		logger.debug("Instances (count): " + instances.size());
		for (InstanceStats instance : instances.values())
		{
			logger.debug(" ");
			instance.dump();
		}
	}
}
