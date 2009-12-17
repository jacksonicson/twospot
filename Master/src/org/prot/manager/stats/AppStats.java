package org.prot.manager.stats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.IntegerStat;
import org.prot.util.stats.StatsUpdater;
import org.prot.util.stats.StatsValue;

public class AppStats
{
	class InstanceStats implements StatsUpdater
	{
		boolean assigned = false;
		long assignmentTimestamp;

		public void update(Set<StatsValue> update)
		{
			for (StatsValue value : update)
			{
				value.update(this);
			}
		}

		@Override
		public void update(String key, IntegerStat value)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void update(String key, DoubleStat value)
		{
			// TODO Auto-generated method stub

		}
	}

	private final String appId;

	public Map<String, InstanceStats> instances = new HashMap<String, InstanceStats>();

	public AppStats(String appId)
	{
		this.appId = appId;
	}

	public String getAppId()
	{
		return this.appId;
	}

	public void update(Set<String> addresses)
	{
		for (Iterator<String> it = instances.keySet().iterator(); it.hasNext();)
		{
			String address = it.next();
			if (!addresses.contains(address))
				it.remove();
		}

		for (String address : addresses)
		{
			if (instances.containsKey(address))
				continue;

			instances.put(address, new InstanceStats());
		}
	}

	public void update(String address, Set<StatsValue> values)
	{
		InstanceStats stats = instances.get(address);
		if (stats == null)
		{
			stats = new InstanceStats();
			instances.put(address, stats);
		}

		stats.update(values);
	}
}
