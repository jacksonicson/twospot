package org.prot.controller.management.jmx;

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

	public void init()
	{
		operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public double loadAverage()
	{
		return operatingSystem.getSystemLoadAverage();
	}

	@Override
	public long freeMemory()
	{
		return operatingSystem.getFreePhysicalMemorySize();
	}

	@Override
	public long requestsPerSecond()
	{
		return countingRequestLog.getCounter();
	}

	@Override
	public Set<String> getApps()
	{
		return appServerWatcher.getRunningApps();

	}

	@Override
	public Set<PerformanceData> getAppsPerformance()
	{
		return appServerWatcher.getAppsPerformance();
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
