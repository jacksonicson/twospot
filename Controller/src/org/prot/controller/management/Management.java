package org.prot.controller.management;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.util.stats.AppStat;
import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.LongStat;
import org.prot.util.stats.StatType;
import org.prot.util.stats.StatsValue;

import ort.prot.util.server.CountingRequestLog;
import sun.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class Management implements IJmxResources
{
	private static final Logger logger = Logger.getLogger(Management.class);

	private AppServerWatcher appServerWatcher;

	private CountingRequestLog countingRequestLog;

	private OperatingSystemMXBean operatingSystem;

	private long timestamp = System.currentTimeMillis();

	public Management()
	{
		operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public String getName()
	{
		return "Controller";
	}

	private long update()
	{
		appServerWatcher.update();

		long time = System.currentTimeMillis() - timestamp;
		if (time > 1000)
		{
			countingRequestLog.reset();
		}

		return time;
	}

	@Override
	public Set<StatsValue> ping()
	{
		Set<StatsValue> data = new HashSet<StatsValue>();

		long time = update();
		double rps = countingRequestLog.getCounter() / (time / 1000);

		data.add(new DoubleStat(StatType.CPU_USAGE, operatingSystem.getSystemLoadAverage()));
		data.add(new LongStat(StatType.FREE_MEMORY, operatingSystem.getFreePhysicalMemorySize()));
		data.add(new LongStat(StatType.TOTAL_MEMORY, operatingSystem.getTotalPhysicalMemorySize()));
		data.add(new DoubleStat(StatType.REQUESTS_PER_SECOND, rps));

		Map<String, Set<StatsValue>> appData = appServerWatcher.getData();
		for (String appId : appData.keySet())
			data.add(new AppStat(StatType.APPLICATION, appId, appData.get(appId)));

		return data;
	}

	public void setAppServerWatcher(AppServerWatcher appServerWatcher)
	{
		this.appServerWatcher = appServerWatcher;
	}

	public void setCountingRequestLog(CountingRequestLog countingRequestLog)
	{
		this.countingRequestLog = countingRequestLog;
	}
}
