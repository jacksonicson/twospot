package org.prot.appserver.runtime.java;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.prot.appserver.management.Management;
import org.prot.util.stats.BooleanStat;
import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.StatType;
import org.prot.util.stats.StatsValue;

import ort.prot.util.server.CountingRequestLog;

public class JettyAppManagement implements Management
{
	private static final Logger logger = Logger.getLogger(JettyAppManagement.class);

	private CountingRequestLog countingRequestLog;

	private Connector connector;

	private ThreadPool pool;

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

		double rps = countingRequestLog.getCounter() / (time / 1000 + 1);

		Set<StatsValue> data = new HashSet<StatsValue>();
		data.add(new BooleanStat(StatType.OVERLOADED, connector.isLowResources()));
		data.add(new DoubleStat(StatType.REQUESTS_PER_SECOND, rps));

		if(pool instanceof QueuedThreadPool)
		{
			QueuedThreadPool p2 = (QueuedThreadPool)pool;
			logger.warn("IS LOW: " + p2.isLowOnThreads());
			logger.warn("WAITING: " + p2.getThreads());
			logger.warn("IDLE: " + pool.getIdleThreads());
		}

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

	public void setThreadPool(ThreadPool pool)
	{
		this.pool = pool;
	}
}
