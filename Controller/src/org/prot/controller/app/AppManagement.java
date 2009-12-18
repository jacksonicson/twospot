package org.prot.controller.app;

import java.util.HashSet;
import java.util.Set;

import org.prot.controller.stats.AppRequestStats;
import org.prot.util.managment.Ping;
import org.prot.util.stats.StatsValue;

public class AppManagement
{
	private Ping ping;

	private final AppRequestStats stats = new AppRequestStats();
	
	private final Set<StatsValue> data = new HashSet<StatsValue>();

	public void update(Set<StatsValue> update)
	{
		data.clear();
		data.addAll(update);
	}
	
	public AppRequestStats getAppRequestStats()
	{
		return stats;
	}

	public Set<StatsValue> getData()
	{
		return data;
	}
	
	public Ping getPing()
	{
		return ping;
	}

	public void setPing(Ping ping)
	{
		this.ping = ping;
	}
}
