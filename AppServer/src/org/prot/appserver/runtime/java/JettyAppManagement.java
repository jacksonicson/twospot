package org.prot.appserver.runtime.java;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.prot.appserver.management.Management;
import org.prot.util.stats.StatsValue;

import ort.prot.util.server.CountingRequestLog;

public class JettyAppManagement implements Management
{
	private static final Logger logger = Logger.getLogger(JettyAppManagement.class);

	private CountingRequestLog countingRequestLog;

	private Connector connector;

	@Override
	public Set<StatsValue> ping()
	{
		return null;
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
