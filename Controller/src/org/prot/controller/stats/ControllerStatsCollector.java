package org.prot.controller.stats;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppRegistry;
import org.prot.controller.config.Configuration;
import org.prot.controller.stats.processors.BalancingProcessor;
import org.prot.util.SystemStats;
import org.prot.util.managment.gen.ManagementData;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class ControllerStatsCollector
{
	private static final Logger logger = Logger.getLogger(ControllerStatsCollector.class);

	private AppRegistry registry;

	private final SystemStats systemStats = new SystemStats();

	private long lastPoll = 0;

	private long requests = 0;

	private List<BalancingProcessor> processors;

	public void init()
	{
		Scheduler.addTask(new StatsTask());
	}

	public void handle(final AppInfo appInfo)
	{
		requests++;
	}

	public void balance()
	{
		for (BalancingProcessor processor : processors)
			processor.run(registry.getDuplicatedAppInfos());
	}

	void update(ManagementData.AppServer appServer)
	{
		AppInfo appInfo = registry.getAppInfo(appServer.getAppId());
		if (appInfo != null)
			appInfo.getAppManagement().update(appServer);
	}

	private float getRps()
	{
		double time = System.currentTimeMillis() - lastPoll;
		double rps = (double) requests / (time / 1000d);
		return (float) rps;
	}

	void fill(ManagementData.Controller.Builder controller)
	{
		controller.setAddress(Configuration.getConfiguration().getAddress());
		controller.setCpu(systemStats.getSystemLoad());
		controller.setProcCpu(systemStats.getProcessLoadSinceLastCall());
		controller.setIdleCpu(systemStats.getSystemIdle());
		controller.setTotalMem(systemStats.getTotalPhysicalMemorySize());
		controller.setFreeMem(systemStats.getFreePhysicalMemorySize());
		controller.setRps(getRps());

		Set<AppInfo> appInfos = registry.getDuplicatedAppInfos();
		controller.setRunningApps(appInfos.size());
		for (AppInfo info : appInfos)
		{
			if (info.getAppManagement().getAppServer() != null)
				controller.addAppServers(info.getAppManagement().getAppServer());
		}

		lastPoll = System.currentTimeMillis();
	}

	class StatsTask extends SchedulerTask
	{
		@Override
		public long getInterval()
		{
			return 5000;
		}

		@Override
		public void run()
		{
			try
			{
				balance();
			} catch (Exception e)
			{
				logger.error("StatsTask failed", e);
				System.exit(1);
			}
		}
	}

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}

	public void setProcessors(List<BalancingProcessor> processors)
	{
		this.processors = processors;
	}
}
