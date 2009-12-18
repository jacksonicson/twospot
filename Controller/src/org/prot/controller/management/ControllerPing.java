package org.prot.controller.management;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.prot.controller.stats.Stats;
import org.prot.util.stats.AppStat;
import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.LongStat;
import org.prot.util.stats.StatType;
import org.prot.util.stats.StatsValue;

public class ControllerPing implements JmxPing
{
	private Stats stats;

	private AppServerWatcher appServerWatcher;

	@Override
	public String getName()
	{
		return "Controller";
	}

	@Override
	public Set<StatsValue> ping()
	{
		appServerWatcher.update();

		Set<StatsValue> data = new HashSet<StatsValue>();

		data.add(new DoubleStat(StatType.CPU_USAGE, stats.getControllerStats().getSystemLoadAverage()));
		data.add(new LongStat(StatType.FREE_MEMORY, stats.getControllerStats().getFrePhysicalMemorySize()));
		data
				.add(new LongStat(StatType.TOTAL_MEMORY, stats.getControllerStats()
						.getTotalPhysicalMemorySize()));
		data.add(new DoubleStat(StatType.REQUESTS_PER_SECOND, stats.getControllerStats().getRps()));

		Map<String, Set<StatsValue>> appStats = stats.getAppStats();
		for (String appId : appStats.keySet())
		{
			AppStat stat = new AppStat(StatType.APPLICATION, appId, appStats.get(appId));
			data.add(stat);
		}

		return data;
	}

	public void setAppServerWatcher(AppServerWatcher appServerWatcher)
	{
		this.appServerWatcher = appServerWatcher;
	}

	public void setStats(Stats stats)
	{
		this.stats = stats;
	}
}
