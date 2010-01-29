package org.prot.appserver.runtime.java;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.prot.appserver.management.RuntimeManagement;
import org.prot.util.managment.gen.ManagementData.AppServer;

import ort.prot.util.server.CountingRequestLog;

public class JettyAppManagement implements RuntimeManagement
{
	private static final Logger logger = Logger.getLogger(JettyAppManagement.class);

	private CountingRequestLog countingRequestLog;

	private Connector connector;

	private int overloadCounter = 0;

	private class Data
	{
		long startTime = 0;
		long stopTime = 0;

		long maxRequests = 0;

		long currentRequests = 0;

		long requestCounter = 0;
	}

	private final int DATAS_LENGTH = 10;
	private LinkedList<Data> datas = new LinkedList<Data>();
	private Data current = null;

	public JettyAppManagement()
	{
		newData();
	}

	private void newData()
	{
		Data data = new Data();
		data.startTime = System.currentTimeMillis();

		datas.add(data);
		current = data;
	}

	private void rollStats()
	{
		long time = System.currentTimeMillis() - current.startTime;
		if (time > 5000)
		{
			current.stopTime = System.currentTimeMillis();

			if (datas.size() >= DATAS_LENGTH)
				datas.remove(0);

			newData();

			countingRequestLog.reset();
			if (connector.isStarted())
				connector.statsReset();
		}
	}

	private void updateData()
	{
		current.stopTime = System.currentTimeMillis();

		current.currentRequests = connector.getRequests();
		if (current.maxRequests < current.currentRequests)
			current.maxRequests = current.currentRequests;

		current.requestCounter = countingRequestLog.getCounter();
	}

	private boolean isOverloaded()
	{
		boolean lowResources = connector.isLowResources();
		if (lowResources)
		{
			overloadCounter = 5;
			return lowResources;
		} else
		{
			lowResources = overloadCounter > 0;
			overloadCounter--;
			return lowResources;
		}
	}

	private float averageLoad()
	{
		double maxLoad = 1d;
		for (Data data : datas)
		{
			double rps = (double) data.requestCounter / ((double) (data.stopTime - data.startTime) / 1000d);
			if (maxLoad < rps)
				maxLoad = rps;
		}

		double rps = averageRps();

		if (datas.size() > 0)
			return (float) (rps / maxLoad);

		return 0f;
	}

	private float averageRps()
	{
		long counter = 0;
		long minTime = Long.MAX_VALUE;
		long maxTime = 0;

		for (Data data : datas)
		{
			counter += data.requestCounter;

			if (minTime > data.startTime)
				minTime = data.startTime;

			if (maxTime < data.stopTime)
				maxTime = data.stopTime;
		}

		if (datas.size() > 0)
			return (float) counter / ((float) (maxTime - minTime) / 1000f);

		return 0f;
	}

	@Override
	public void fill(AppServer.Builder appServer)
	{
		updateData();

		appServer.setLoad(averageLoad());
		appServer.setRps(averageRps());
		appServer.setOverloaded(isOverloaded());

		rollStats();
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
