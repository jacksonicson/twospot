package org.prot.controller.stats;

import sun.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class ControllerStats
{
	private OperatingSystemMXBean operatingSystem;

	private RpsCounter rpsCounter = new RpsCounter();

	public ControllerStats()
	{
		operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	public void handle()
	{
		rpsCounter.count();
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

	public double getRps()
	{
		return rpsCounter.getRps();
	}
}
