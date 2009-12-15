package org.prot.controller.management.appserver;

import java.io.Serializable;

public class PerformanceData implements Serializable
{
	private static final long serialVersionUID = 8203276272151895670L;

	private final String appId;

	private double requestsPerSecond;

	private double averageRequestTime;

	private double load;

	public PerformanceData(String appId)
	{
		this.appId = appId;
	}

	public double getRequestsPerSecond()
	{
		return requestsPerSecond;
	}

	public void setRequestsPerSecond(double requestsPerSecond)
	{
		this.requestsPerSecond = requestsPerSecond;
	}

	public double getAverageRequestTime()
	{
		return averageRequestTime;
	}

	public void setAverageRequestTime(double averageRequestTime)
	{
		this.averageRequestTime = averageRequestTime;
	}

	public double getLoad()
	{
		return load;
	}

	public void setLoad(double load)
	{
		this.load = load;
	}

	public String getAppId()
	{
		return appId;
	}
}
