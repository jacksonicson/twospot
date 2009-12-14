package org.prot.controller.management.services;

import org.apache.log4j.Logger;
import org.prot.controller.management.appserver.AppServerWatcher;
import org.prot.controller.management.appserver.PerformanceData;

import ort.prot.util.server.CountingRequestLog;
import sun.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class JmxResources implements IJmxResources
{
	private static final Logger logger = Logger.getLogger(JmxResources.class);

	private AppServerWatcher appServerWatcher;

	private CountingRequestLog countingRequestLog;

	private OperatingSystemMXBean operatingSystem;

	private long requestTime = System.currentTimeMillis();

	public JmxResources()
	{
		operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public String getName()
	{
		return "Resources";
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
	public double requestsPerSecond()
	{
		long requests = countingRequestLog.getCounter();
		countingRequestLog.reset();

		long time = System.currentTimeMillis() - requestTime;
		requestTime = System.currentTimeMillis();

		double rps = requests / (time / 1000d);

		return rps;
	}

	@Override
	public String[] getApps()
	{
		return appServerWatcher.getRunningApps();
	}

	@Override
	public PerformanceData[] getAppsPerformance()
	{
		return appServerWatcher.getAppsPerformance();
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
