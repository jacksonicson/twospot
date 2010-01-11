package org.prot.appserver.runtime.java;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.prot.appserver.management.Management;
import org.prot.util.managment.gen.ManagementData.AppServer;

import ort.prot.util.server.CountingRequestLog;

public class JettyAppManagement implements Management
{
	private static final Logger logger = Logger.getLogger(JettyAppManagement.class);

	private CountingRequestLog countingRequestLog;

	private Connector connector;

	private long timestamp = System.currentTimeMillis();

	private long update()
	{
		long time = System.currentTimeMillis() - timestamp;

		if (System.currentTimeMillis() - timestamp > 10000)
		{
			timestamp = System.currentTimeMillis();
			connector.statsReset();
			countingRequestLog.reset();
		}

		return time;
	}

	@Override
	public void fill(AppServer.Builder appServer)
	{
		long time = update();

		appServer.setLoad(0f);
		appServer.setRps(countingRequestLog.getCounter() / (time / 1000 + 1));
		appServer.setOverloaded(connector.isLowResources());
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
