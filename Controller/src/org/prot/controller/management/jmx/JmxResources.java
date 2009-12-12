package org.prot.controller.management.jmx;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.prot.controller.management.AppServerWatcher;

import com.sun.management.OperatingSystemMXBean;

public class JmxResources implements IJmxResources
{
	private static final Logger logger = Logger.getLogger(JmxResources.class);

	private static final String NAME = "Resources";

	private AppServerWatcher management;

	private Connector connector;

	private RequestLogHandler logHandler;

	@Override
	public long loadAverage()
	{
		return 0;
	}

	@Override
	public long requestsPerSecond()
	{
		return management.getRps();
	}

	@Override
	public long runningAppServers()
	{
		return 0;
	}

	@Override
	public Set<String> getApps()
	{
		SelectChannelConnector sc = (SelectChannelConnector) connector;

		logger.warn("Connections open since last poll: " + sc.getConnectionsOpen());
		sc.statsReset();

		MyLog log = (MyLog) logHandler.getRequestLog();
		logger.warn("Requests handled since last poll: " + log.getLog());

		OperatingSystemMXBean osm = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		logger.warn("CPU Usage: " + osm.getSystemLoadAverage());
		logger.warn("Memory: " + osm.getFreePhysicalMemorySize());

		Set<String> apps = new HashSet<String>();
		apps.addAll(management.getRunningApps());
		return apps;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	public void setManagement(AppServerWatcher management)
	{
		this.management = management;
	}

	public void setConnector(Connector connector)
	{
		this.connector = connector;
	}

	class MyLog implements RequestLog
	{
		int request = 0;

		@Override
		public void log(Request request, Response response)
		{
			this.request++;
		}

		public int getLog()
		{
			int buffer = this.request;
			this.request = 0;
			return buffer;
		}

		@Override
		public void addLifeCycleListener(Listener listener)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isFailed()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRunning()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isStarted()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isStarting()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isStopped()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isStopping()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeLifeCycleListener(Listener listener)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void start() throws Exception
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void stop() throws Exception
		{
			// TODO Auto-generated method stub

		}

	}

	public void setLogHandler(RequestLogHandler logHandler)
	{
		this.logHandler = logHandler;
		this.logHandler.setRequestLog(new MyLog());
	}
}
