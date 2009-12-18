package org.prot.controller.management;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.stats.Stats;
import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.LongStat;
import org.prot.util.stats.StatType;
import org.prot.util.stats.StatsValue;

import sun.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class Management implements JmxPing
{
	private static final Logger logger = Logger.getLogger(Management.class);

	private Stats stats;

	private AppServerWatcher appServerWatcher;

	private OperatingSystemMXBean operatingSystem;

	public Management()
	{
		operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public String getName()
	{
		return "Controller";
	}

	private void update()
	{
		appServerWatcher.update();
	}

	@Override
	public Set<StatsValue> ping()
	{
		update();

		Set<StatsValue> data = new HashSet<StatsValue>();

		double rps = 0;

		data.add(new DoubleStat(StatType.CPU_USAGE, operatingSystem.getSystemLoadAverage()));
		data.add(new LongStat(StatType.FREE_MEMORY, operatingSystem.getFreePhysicalMemorySize()));
		data.add(new LongStat(StatType.TOTAL_MEMORY, operatingSystem.getTotalPhysicalMemorySize()));
		data.add(new DoubleStat(StatType.REQUESTS_PER_SECOND, rps));

		// Map<String, Set<StatsValue>> appData = logger.debug("AppServers: " +
		// appData.size());
		// for (String appId : appData.keySet())
		// data.add(new AppStat(StatType.APPLICATION, appId,
		// appData.get(appId)));

		return data;
	}

	public void setAppServerWatcher(AppServerWatcher appServerWatcher)
	{
		this.appServerWatcher = appServerWatcher;
	}
}
