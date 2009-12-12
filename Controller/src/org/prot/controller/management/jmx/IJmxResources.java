package org.prot.controller.management.jmx;

import java.util.Set;

import org.prot.controller.management.PerformanceData;

public interface IJmxResources
{
	public double loadAverage();

	public long freeMemory();

	public long requestsPerSecond();

	public Set<String> getApps();

	public Set<PerformanceData> getAppsPerformance();

	public String getName();
}
