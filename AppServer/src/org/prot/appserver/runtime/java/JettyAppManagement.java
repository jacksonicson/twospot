package org.prot.appserver.runtime.java;

import org.eclipse.jetty.server.Connector;
import org.prot.appserver.management.RuntimeManagement;
import org.prot.util.SystemStats;
import org.prot.util.managment.gen.ManagementData.AppServer;

import ort.prot.util.server.CountingRequestLog;

public class JettyAppManagement implements RuntimeManagement
{
	private long startTime = System.currentTimeMillis();

	private long lastPoll = 0;

	private SystemStats systemStats = new SystemStats();

	private CountingRequestLog countingRequestLog;

	private Connector connector;

	private long getRuntime()
	{
		return System.currentTimeMillis() - startTime;
	}

	private float getRps()
	{
		long time = System.currentTimeMillis() - lastPoll;
		double rps = (double) countingRequestLog.getCounter() / (double) (time / 1000 + 1);
		return (float) rps;
	}

	private float getDelay()
	{
		double delay = (double) countingRequestLog.getSummedRequestTime()
				/ ((double) countingRequestLog.getCounter() + 1d);
		return (float) delay;
	}

	private boolean isLowResources()
	{
		return connector.isLowResources();
	}

	@Override
	public void fill(AppServer.Builder appServer)
	{
		appServer.setRuntime(getRuntime());
		appServer.setProcCpu(systemStats.getProcessLoadSinceLastCall());
		appServer.setCpuTotal(systemStats.getCpuTotal());
		appServer.setCpuProcTotal(systemStats.getProcTotal());
		appServer.setRps(getRps());
		appServer.setAverageDelay(getDelay());
		appServer.setOverloaded(isLowResources());

		lastPoll = System.currentTimeMillis();
		countingRequestLog.reset();
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
