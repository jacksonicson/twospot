package org.prot.controller.management;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppRegistry;
import org.prot.controller.stats.Stats;
import org.prot.util.stats.AppStat;
import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.LongStat;
import org.prot.util.stats.StatType;
import org.prot.util.stats.StatsValue;

import sun.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class Management implements JmxPing
{
	private static final Logger logger = Logger.getLogger(Management.class);

	private AppRegistry registry;

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

		for (String appId : registry.getAppIds())
		{
			AppInfo info = registry.getAppInfo(appId);
			Set<StatsValue> appData = info.getAppManagement().getData();
			AppStat stat = new AppStat(StatType.APPLICATION, appId, appData);
			data.add(stat);
		}

		return data;
	}

	public void setAppServerWatcher(AppServerWatcher appServerWatcher)
	{
		this.appServerWatcher = appServerWatcher;
	}

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}

	public void setStats(Stats stats)
	{
		this.stats = stats;
	}
}
