package org.prot.controller.management.services;

import org.prot.controller.management.appserver.PerformanceData;

public interface IJmxResources
{
	public double loadAverage();

	public double freeMemory();

	public double requestsPerSecond();

	public String[] getApps();

	public PerformanceData[] getAppsPerformance();

	public String getName();
}
