package org.prot.manager.stats;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.IntegerStat;
import org.prot.util.stats.StatsUpdater;
import org.prot.util.stats.StatsValue;

public class ControllerStats implements StatsUpdater
{
	// Identifies the controller
	private final String address;

	// AppId's which are managed by the Controller
	private Set<String> runningApps = new HashSet<String>();

	class StatValues
	{

	}

	private final StatValues stats = new StatValues();

	public ControllerStats(String address)
	{
		this.address = address;
	}

	void updateRunningApps(Set<String> update)
	{
		for (Iterator<String> it = runningApps.iterator(); it.hasNext();)
		{
			String appId = it.next();
			if (!update.contains(appId))
				it.remove();
		}

		runningApps.addAll(update);
	}

	void updateStats(Set<StatsValue> update)
	{
		for (StatsValue value : update)
		{
			value.update(this);
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

	@Override
	public void update(String key, IntegerStat value)
	{

	}

	@Override
	public void update(String key, DoubleStat value)
	{

	}
}
