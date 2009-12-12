package org.prot.appserver.runtime.java;

import org.prot.appserver.management.AppManagement;

public class JettyAppManagement implements AppManagement
{
	private CountingRequestLog countingRequestLog;

	private long lastReset = 0;

	public void init()
	{
		this.lastReset = System.currentTimeMillis();
	}

	@Override
	public long connectionCount()
	{
		return -1;
	}

	@Override
	public long requestCount()
	{
		return countingRequestLog.getCounter();
	}

	@Override
	public double requestsPerSecond()
	{
		return countingRequestLog.getCounter() / (System.currentTimeMillis() - lastReset + 1);
	}

	@Override
	public void reset()
	{
		countingRequestLog.reset();
		lastReset = System.currentTimeMillis();
	}

	public void setCountingRequestLog(CountingRequestLog countingRequestLog)
	{
		this.countingRequestLog = countingRequestLog;
	}
}
