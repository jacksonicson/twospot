package org.prot.controller.management;

import java.io.Serializable;

import org.prot.controller.manager.appserver.IAppServerStats;

public class PerformanceData implements Serializable
{
	private static final long serialVersionUID = 8203276272151895670L;

	private transient IAppServerStats connection;

	private final String appId;

	private double rps;

	public PerformanceData(String appId)
	{
		this.appId = appId;
	}

	public double getRps()
	{
		return rps;
	}

	public void setRps(double rps)
	{
		this.rps = rps;
	}

	public String getAppId()
	{
		return appId;
	}

	IAppServerStats getConnection()
	{
		return connection;
	}

	void setConnection(IAppServerStats connection)
	{
		this.connection = connection;
	}
}
