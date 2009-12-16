package org.prot.controller.management.appserver;

import java.io.Serializable;

public class PerformanceData implements Serializable
{
	private static final long serialVersionUID = 8203276272151895670L;

	private final String appId;

	private double requestsPerSecond = 0d;

	private double averageRequestTime = 0d;

	private double load = 0d;

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
