package org.prot.appserver.runtime.winstone;

import org.eclipse.jetty.server.Connector;
import org.prot.appserver.management.RuntimeManagement;
import org.prot.util.SystemStats;
import org.prot.util.managment.gen.ManagementData.AppServer;

import ort.prot.util.server.CountingRequestLog;

public class WinstoneAppManagement implements RuntimeManagement
{
	private long startTime = System.currentTimeMillis();

	private long lastPoll = 0;

	private SystemStats systemStats = new SystemStats();

	private Connector connector;

	private long getRuntime()
	{
		return System.currentTimeMillis() - startTime;
	}

	private float getRps()
	{
		return 0; 
	}

	private float getDelay()
	{
		return 0; 
	}

	private boolean isLowResources()
	{
		return false; 
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

	}

	public void setCountingRequestLog(CountingRequestLog countingRequestLog)
	{
	}

	public void setConnector(Connector connector)
	{
		this.connector = connector;
	}
}
