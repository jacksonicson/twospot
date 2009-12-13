package org.prot.manager.data;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.management.PerformanceData;

public class ManagementData
{
	private static final Logger logger = Logger.getLogger(ManagementData.class);

	// Requests per second which are processed by the Controller
	private double rps;

	// Memory consumed by the AppServer's
	private long memLoad;

	// Average CPU usage
	private double averageCpu;

	// Data transfered by the Controller (in and out) (bytes per second)
	private long networkTraffic;

	// AppId's which are running under the Controller
	private Set<String> runningApps;

	// Performance data of each AppServer
	private Set<PerformanceData> performanceData;

	public double getRps()
	{
		return rps;
	}

	public void setRps(double rps)
	{
		this.rps = rps;
	}

	public Set<String> getRunningApps()
	{
		return runningApps;
	}

	public void setRunningApps(Set<String> runningApps)
	{
		this.runningApps = runningApps;
	}

	public long getMemLoad()
	{
		return memLoad;
	}

	public void setMemLoad(long memLoad)
	{
		this.memLoad = memLoad;
	}

	public double getAverageCpu()
	{
		return averageCpu;
	}

	public void setAverageCpu(double averageCpu)
	{
		this.averageCpu = averageCpu;
	}

	public long getNetworkTraffic()
	{
		return networkTraffic;
	}

	public void setNetworkTraffic(long networkTraffic)
	{
		this.networkTraffic = networkTraffic;
	}

	public Set<PerformanceData> getPerformanceData()
	{
		return performanceData;
	}

	public void setPerformanceData(Set<PerformanceData> performanceData)
	{
		this.performanceData = performanceData;
	}

	public void dump()
	{
		logger.info("Requests per second: " + rps);
		logger.info("Memload: " + memLoad);
		logger.info("Average CPUload:" + averageCpu);
		logger.info("Network traffic: " + networkTraffic);

		for (String app : runningApps)
			logger.info("Running AppId: " + app);

		for (PerformanceData per : performanceData)
		{
			logger.info("Details for: " + per.getAppId());
			logger.info("Requests per second: " + per.getRps());
		}
	}
}
