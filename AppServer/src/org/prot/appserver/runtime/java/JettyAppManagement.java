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
	public long connectionCount()
	{
		// TODO:
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
		long count = countingRequestLog.getCounter();
		long time = System.currentTimeMillis() - lastReset;
		reset();
		return count / (time / 1000d);
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
