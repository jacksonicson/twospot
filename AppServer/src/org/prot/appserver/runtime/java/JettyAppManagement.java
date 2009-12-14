package org.prot.appserver.runtime.java;

import org.prot.appserver.management.AppManagement;

import ort.prot.util.server.CountingRequestLog;

public class JettyAppManagement implements AppManagement
{
	private CountingRequestLog countingRequestLog;

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
		return count / (time / 1000d);
	}

	@Override
	public long averageRequestTime()
	{
		return 0;
	}

	public void setCountingRequestLog(CountingRequestLog countingRequestLog)
	{
		this.countingRequestLog = countingRequestLog;
	}
}
