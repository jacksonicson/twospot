package org.prot.controller.stats;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppRegistry;
import org.prot.controller.config.Configuration;
import org.prot.controller.stats.processors.BalancingProcessor;
import org.prot.util.managment.gen.ManagementData;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class ControllerStatsCollector
{
	private static final Logger logger = Logger.getLogger(ControllerStatsCollector.class);

	private AppRegistry registry;

	private final SystemStats systemStats = new SystemStats();

	private final RpsCounter rpsCounter = new RpsCounter();

	private List<BalancingProcessor> processors;

	public void init()
	{
		Scheduler.addTask(new StatsTask());
	}

	public void handle(final AppInfo appInfo)
	{
		try
		{
			appInfo.getAppManagement().getStats();
			rpsCounter.count();
		} catch (Exception e)
		{
			logger.error("Error while handling stas: " + e);
		}
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

	void fill(ManagementData.Controller.Builder controller)
	{
		controller.setAddress(Configuration.getConfiguration().getAddress());

		controller.setCpu((float) systemStats.getSystemLoadAverage());
		controller.setFreeMem(systemStats.getFrePhysicalMemorySize());
		controller.setTotalMem(systemStats.getTotalPhysicalMemorySize());

		for (AppInfo info : registry.getDuplicatedAppInfos())
		{
			if (info.getAppManagement().getAppServer() != null)
			{
				logger.debug("Controller is adding an Management AppInfo");
				controller.addAppServers(info.getAppManagement().getAppServer());
			}
		}
	}

	class StatsTask extends SchedulerTask
	{
		@Override
		public long getInterval()
		{
			return 3000;
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
