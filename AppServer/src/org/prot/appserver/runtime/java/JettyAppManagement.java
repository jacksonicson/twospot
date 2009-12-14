package org.prot.appserver.runtime.java;

import org.eclipse.jetty.server.Connector;
import org.prot.appserver.management.AppManagement;

import ort.prot.util.server.CountingRequestLog;

public class JettyAppManagement implements AppManagement
{
	private CountingRequestLog countingRequestLog;

	private Connector connector;

	private long lastReset = 0;

	public JettyAppManagement()
	{
		this.lastReset = System.currentTimeMillis();
	}

	@Override
	public double requestsPerSecond()
	{
		long count = countingRequestLog.getCounter();
		long time = System.currentTimeMillis() - lastReset;

		lastReset = System.currentTimeMillis();
		countingRequestLog.reset();

		return count / (time / 1000d);
	}

	@Override
	public long averageRequestTime()
	{
		return -1;
	}

	public void setCountingRequestLog(CountingRequestLog countingRequestLog)
	{
		this.countingRequestLog = countingRequestLog;
	}

	public void setConnector(Connector connector)
	{
		this.connector = connector;
	}
}
