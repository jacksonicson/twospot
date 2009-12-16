package org.prot.manager.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.management.appserver.PerformanceData;

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
	private Set<String> runningApps = new HashSet<String>();

	// Performance data of each AppServer
	private Map<String, PerformanceData> performanceData = new HashMap<String, PerformanceData>();

	public void updateRunningApps(String[] update)
	{
		// Remove old entries
		for (String running : runningApps.toArray(new String[0]))
		{
			boolean found = false;
			for (String test : update)
				if (test.equals(running))
				{
					found = true;
					break;
				}

			if (found == false)
				runningApps.remove(running);
		}

		// Add new entries
		for (String app : update)
		{
			if (!runningApps.contains(app))
				runningApps.add(app);
		}
	}

	public void updatePerformanceData(PerformanceData[] update)
	{
		for (PerformanceData test : update)
		{
			String appId = test.getAppId();
			if (performanceData.containsKey(appId))
			{
				PerformanceData toUpdate = performanceData.get(appId);
				toUpdate.setAverageRequestTime(test.getAverageRequestTime());
				toUpdate.setRequestsPerSecond(test.getRequestsPerSecond());
				toUpdate.setLoad(test.getLoad());
			} else
			{
				performanceData.put(appId, test);
			}
		}
	}

	public double getRps()
	{
		return rps;
	}

	public void setRps(double rps)
	{
		this.rps = rps;
	}

	public String[] getRunningApps()
	{
		return (String[]) runningApps.toArray(new String[0]);
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
		// Windows-Systems return -1 which is not very useful!
		if (averageCpu < 0)
			averageCpu = 0;

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

	public PerformanceData[] getPerformanceData()
	{
		return (PerformanceData[]) performanceData.values().toArray(new PerformanceData[0]);
	}

	public PerformanceData getPerformanceData(String appId)
	{
		return performanceData.get(appId);
	}

	public void dump()
	{
		logger.info("Requests per second: " + rps);
		logger.info("Memload: " + memLoad);
		logger.info("Average CPUload:" + averageCpu);
		logger.info("Network traffic: " + networkTraffic);

		for (String app : runningApps)
			logger.info("Running AppId: " + app);

		for (PerformanceData per : performanceData.values())
		{
			logger.info("   Details for: " + per.getAppId());
			logger.info("   Requests per second: " + per.getRequestsPerSecond());
			logger.info("   Requests per second: " + per.getAverageRequestTime());
		}
	}
}
