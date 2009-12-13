package org.prot.controller.management.jmx;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.management.AppServerWatcher;
import org.prot.controller.management.PerformanceData;

import ort.prot.util.server.CountingRequestLog;
import sun.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class JmxResources implements IJmxResources
{
	private static final Logger logger = Logger.getLogger(JmxResources.class);

	private static final String NAME = "Resources";

	private AppServerWatcher appServerWatcher;

	private CountingRequestLog countingRequestLog;

	private OperatingSystemMXBean operatingSystem;

	private long requestTime = System.currentTimeMillis();

	public void init()
	{
		operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public double loadAverage()
	{
		double load = operatingSystem.getSystemLoadAverage();
		return load;
	}

	@Override
	public long freeMemory()
	{
		long free = operatingSystem.getFreePhysicalMemorySize();
		return free;
	}

	@Override
	public double requestsPerSecond()
	{
		long requests = countingRequestLog.getCounter();
		long time = System.currentTimeMillis() - requestTime;
		return requests / (time / 1000d);
	}

	@Override
	public Set<String> getApps()
	{
		Set<String> apps = new HashSet<String>();
		apps.addAll(appServerWatcher.getRunningApps());

		return apps;
	}

	@Override
	public Set<PerformanceData> getAppsPerformance()
	{
		Set<PerformanceData> performance = new HashSet<PerformanceData>();
		performance.addAll(appServerWatcher.getAppsPerformance());
		return performance;
	}

	@Override
	public String getName()
	{
		return NAME;
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
