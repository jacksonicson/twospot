package org.prot.controller.stats;

import sun.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class SystemStats
{
	private OperatingSystemMXBean operatingSystem;

	public SystemStats()
	{
		operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	public double getSystemLoadAverage()
	{
		return operatingSystem.getSystemLoadAverage();
	}

	public long getFrePhysicalMemorySize()
	{
		return operatingSystem.getFreePhysicalMemorySize();
	}

	public long getTotalPhysicalMemorySize()
	{
		return operatingSystem.getTotalPhysicalMemorySize();
	}
}
