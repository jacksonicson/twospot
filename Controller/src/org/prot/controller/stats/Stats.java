package org.prot.controller.stats;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppRegistry;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class Stats
{
	private static final Logger logger = Logger.getLogger(Stats.class);

	private AppRegistry registry;

	private List<BalancingProcessor> processors;

	public void init()
	{
		Scheduler.addTask(new StatsTask());
	}

	public void handle(final String appId)
	{
		AppRequestStats stats = registry.getAppInfo(appId).getAppManagement().getAppRequestStats();
		stats.handle();
	}

	public void balance()
	{
		logger.debug("Executing the balancing processors...");

		for (BalancingProcessor processor : processors)
			processor.run(registry.getAppInfos());
	}

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}

	public void setProcessors(List<BalancingProcessor> processors)
	{
		this.processors = processors;
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
}
