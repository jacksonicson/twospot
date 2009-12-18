package org.prot.manager.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.util.stats.AppStat;
import org.prot.util.stats.BooleanStat;
import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.IntegerStat;
import org.prot.util.stats.LongStat;
import org.prot.util.stats.StatType;
import org.prot.util.stats.StatsUpdater;
import org.prot.util.stats.StatsValue;

public class ControllerStats implements StatsUpdater
{
	private static final Logger logger = Logger.getLogger(ControllerStats.class);

	// Identifies the controller
	private final String address;

	// Last update
	private long lastUpdate;

	// AppId's which are managed by the Controller
	private Set<String> runningApps = new HashSet<String>();

	// All
	private Map<String, InstanceStats> instances = new HashMap<String, InstanceStats>();

	public class StatValues
	{
		public double cpu;
		public double rps;

		public long freeMemory;
		public long totalMemory;

		public void dump()
		{
			logger.debug("   CPU: " + cpu);
			logger.debug("   RPS: " + rps);
			logger.debug("   FreeMem: " + freeMemory);
			logger.debug("   TotalMem: " + totalMemory);
		}
	}

	private final StatValues stats = new StatValues();

	public ControllerStats(String address)
	{
		this.address = address;
	}

	public int countStartedApps()
	{
		return instances.size();
	}

	public int size()
	{
		return instances.size();
	}

	public InstanceStats getInstance(String appId)
	{
		return instances.get(appId);
	}

	boolean isOld()
	{
		return System.currentTimeMillis() - lastUpdate > 60 * 1000;
	}

	Set<String> getRunningApps()
	{
		return instances.keySet();
	}

	void assign(String appId)
	{
		if (instances.containsKey(appId))
			return;

		instances.put(appId, new InstanceStats(appId, true));
	}

	void updateStats(Set<StatsValue> update)
	{
		lastUpdate = System.currentTimeMillis();

		runningApps.clear();

		for (StatsValue value : update)
			value.update(this);

		for (Iterator<String> it = instances.keySet().iterator(); it.hasNext();)
		{
			// Remove everything which is not running or old
			String appId = it.next();
			if (!runningApps.contains(appId))
			{
				logger.debug("Removing: " + appId);
				it.remove();
			} else if (instances.get(appId).isOld())
			{
				logger.debug("Old instance");
				it.remove();
			}
		}
	}

	public StatValues getValues()
	{
		return stats;
	}

	public String getAddress()
	{
		return this.address;
	}

	public void update(StatType key, IntegerStat value)
	{
		// Empty
	}

	@Override
	public void update(StatType key, DoubleStat value)
	{
		switch (key)
		{
		case CPU_USAGE:
			stats.cpu = value.get();
			break;
		case REQUESTS_PER_SECOND:
			stats.rps = value.get();
			break;
		}
	}

	@Override
	public void update(StatType key, BooleanStat value)
	{
		// Empty
	}

	@Override
	public void update(StatType key, LongStat value)
	{
		switch (key)
		{
		case FREE_MEMORY:
			stats.freeMemory = value.get();
			break;
		case TOTAL_MEMORY:
			stats.totalMemory = value.get();
			break;
		}
	}

	@Override
	public void update(StatType key, AppStat value)
	{
		switch (key)
		{
		case APPLICATION:
			final String appId = value.get();
			runningApps.add(appId);

			if (!instances.containsKey(appId))
				instances.put(appId, new InstanceStats(appId));

			InstanceStats instance = instances.get(appId);
			instance.update(value.composite());
			break;
		}
	}

	public void dump()
	{
		logger.debug("RunningApps: " + runningApps.size());
		stats.dump();
		logger.debug("Instances: " + instances.size());
		for (InstanceStats instance : instances.values())
			instance.dump();
	}
}
