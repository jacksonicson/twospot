package org.prot.appserver.runtime.java;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.server.Connector;
import org.prot.appserver.management.Management;
import org.prot.util.stats.BooleanStat;
import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.StatsValue;

import ort.prot.util.server.CountingRequestLog;

public class JettyAppManagement implements Management
{
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
	public Set<StatsValue> ping()
	{
		long time = update();

		double rps = countingRequestLog.getCounter() / (time / 1000);

		Set<StatsValue> data = new HashSet<StatsValue>();
		data.add(new BooleanStat("overloaded", connector.isLowResources()));
		data.add(new DoubleStat("rps", rps));

		return data;
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
