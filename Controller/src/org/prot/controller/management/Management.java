package org.prot.controller.management;

import java.util.Set;

import org.apache.log4j.Logger;
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

	public Management()
	{
		operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	public String getName()
	{
		return "Controller";
	}

	@Override
	public Set<StatsValue> ping()
	{
		return null;
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
